package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.LoginCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.policy.LogoutPolicy;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.LoginResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.TokenResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.usecase.AuthCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.exception.AuthException;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.FailureCountGuard;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.jwt.JwtTokenProvider;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.token.RefreshTokenService;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.token.TokenBlacklistService;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.user.PrincipalCache;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Member;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.MemberStatus;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Locale;

/*
 * comment.
 *  로그인 / 로그아웃 / 토큰 재발급을 처리하는 인증 서비스
 *  - 클래스에 @Transactional을 걸지 않았다.
 *    로그인은 DB를 읽기만 하고, BCrypt 비교는 CPU를 오래 쓰는 작업이라
 *    트랜잭션을 열면 그 시간 동안 DB 커넥션을 붙잡고 있게 된다.
 *  - 실패 횟수/토큰/인증 캐시는 모두 Redis에 있어서 DB 트랜잭션과 무관하다.
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthCommandService implements AuthCommandUsecase {

    // 로그인 실패 횟수를 세는 Redis 키의 구분자 (failure:login:{email}, lock:login:{email})
    private static final String LOGIN_FAILURE_SCOPE = "login";
    // 이 횟수만큼 틀리면 잠금
    private static final int LOGIN_MAX_FAILURE_COUNT = 5;
    // 실패 횟수를 세는 기간 (첫 실패 후 10분이 지나면 카운터 리셋)
    private static final Duration LOGIN_FAILURE_WINDOW = Duration.ofMinutes(10);
    // 잠금 유지 시간
    private static final Duration LOGIN_LOCK_DURATION = Duration.ofMinutes(15);

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;
    private final FailureCountGuard failureCountGuard;
    private final PrincipalCache principalCache;
    private final LogoutPolicy logoutPolicy;

    /*
     * comment.
     *  존재하지 않는 이메일로 로그인할 때 비교에 쓰는 가짜 해시.
     *  이메일이 없으면 BCrypt 비교를 건너뛰어 응답이 빨라지고,
     *  그 응답 시간 차이로 "이 이메일은 가입되어 있다"를 알아낼 수 있다(타이밍 공격).
     *  없는 이메일에도 BCrypt 비교를 한 번 수행해서 시간 차이를 없앤다.
     * */
    private String dummyHash;

    @PostConstruct
    void init() {
        // 서버 기동 시 한 번만 만들어 둔다 (BCrypt 해시 생성은 느려서 매 요청마다 만들면 안 됨)
        this.dummyHash = passwordEncoder.encode("timing-attack-dummy-password");
    }

    /*
     * comment.
     *  로그인
     *  검증 순서: 잠금 확인 → 회원 조회 → 탈퇴 여부 → 비밀번호 보유 여부 → 비밀번호 일치
     *  성공하면 access token(응답 본문용)과 refresh token(쿠키용)을 발급한다.
     *  "로그인 상태 유지" 체크 여부에 따라 refresh token 유효기간이 달라진다.
     * */
    @Override
    public LoginResult login(LoginCommand command) {
        // 대소문자/공백이 달라도 같은 계정으로 인식하도록 정규화
        String email = normalizeEmail(command.email());
        String clientIp = command.clientIp();

        // 1. 잠긴 계정이면 비밀번호 검증 없이 바로 차단 (잠금 중에는 BCrypt 비용도 쓰지 않음)
        if (failureCountGuard.isLocked(LOGIN_FAILURE_SCOPE, email)) {
            throw AuthException.accountLocked();
        }

        Member member = memberRepository.findByEmail(email).orElse(null);

        // 2. 없는 이메일: 가짜 해시와 비교해 시간을 맞춘 뒤, 비밀번호 오류와 같은 메시지로 실패 처리
        //    (이메일이 없는지 비밀번호가 틀린 건지 외부에서 구분할 수 없게 한다)
        if (member == null) {
            passwordEncoder.matches(command.password(), dummyHash);
            throw loginFailed(email, clientIp, "NOT_FOUND", AuthException.loginFailed());
        }

        // 3. 구글 가입 계정(비밀번호 없음): 안내 문구는 제품 정책으로 유지하되,
        //    응답 시간으로 구분되지 않게 가짜 해시와 비교해 BCrypt 시간을 맞춘다
        if (!member.hasPassword()) {
            passwordEncoder.matches(command.password(), dummyHash);
            throw loginFailed(email, clientIp, "NO_PASSWORD", AuthException.socialAccountNoPassword());
        }

        // 4. 비밀번호 불일치 (탈퇴 계정도 틀리면 일반 실패와 같은 응답이라 탈퇴 여부가 노출되지 않음)
        if (!passwordEncoder.matches(command.password(), member.getPassword())) {
            throw loginFailed(email, clientIp, "BAD_PASSWORD", AuthException.loginFailed());
        }

        // 5. 비밀번호가 맞은 뒤에만 탈퇴 여부를 알려준다 (본인임이 확인된 사용자에게만 노출)
        if (member.getStatus() == MemberStatus.WITHDRAWN) {
            throw AuthException.withdrawnMember();
        }

        // 6. 로그인 성공: 실패 카운터 초기화
        failureCountGuard.resetFailure(LOGIN_FAILURE_SCOPE, email);

        // 7. 인증 필터가 매 요청마다 DB를 읽지 않도록 회원 정보를 Redis에 캐시
        principalCache.put(member);

        // 8. refresh token 유효기간 결정: 체크하면 14일, 체크 안 하면 1일
        Duration refreshTtl = command.rememberMe()
                ? jwtTokenProvider.getRefreshTokenValidity()
                : jwtTokenProvider.getSessionRefreshTokenValidity();

        // 9. 토큰 발급 (refresh token의 만료시각도 위에서 정한 기간과 같게 만든다)
        String accessToken = jwtTokenProvider.createAccessToken(member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId(), refreshTtl);

        // 10. refresh token을 Redis에 저장 (회원당 1개: 다른 기기에서 로그인하면 덮어써짐)
        refreshTokenService.save(member.getId(), refreshToken, refreshTtl);

        return new LoginResult(accessToken, refreshToken, refreshTtl,
                command.rememberMe(), member.getRole(), member.getNickname());
    }

    /*
     * comment.
     *  로그아웃
     *  access token은 만료 전까지 유효한 JWT라서 서버가 직접 무효화할 수 없다.
     *  그래서 토큰의 jti(고유 id)를 남은 유효시간만큼 블랙리스트에 올리고,
     *  refresh token도 지워서 재발급이 불가능하게 만든다.
     * */
    @Override
    public void logout(Long memberId, String bearerToken, String refreshToken) {
        // Authorization 헤더에서 access token 추출 (형식이 틀리면 예외)
        String accessToken = LogoutPolicy.resolveAccessToken(bearerToken);

        // 1) refresh token을 먼저 삭제 (쿠키가 있고 Redis 값과 일치할 때만)
        //    access token을 먼저 블랙리스트에 올리면, 이 단계가 실패했을 때 재시도 요청이 인증에서 막혀
        //    refresh token이 남아버린다. 삭제를 먼저 해야 실패해도 같은 요청으로 다시 시도할 수 있다.
        //    (null 체크가 없으면 isValid()에서 NPE, 일치 검사가 없으면 다른 기기의 세션을 지울 수 있음)
        if (refreshToken != null && refreshTokenService.isValid(memberId, refreshToken)) {
            refreshTokenService.delete(memberId);
        }

        // 2) access token 블랙리스트 등록 (refresh 쿠키 상태와 무관하게 반드시 수행)
        Claims claims = jwtTokenProvider.resolveAccessClaims(accessToken);
        if (claims != null) {
            tokenBlacklistService.blacklist(
                    jwtTokenProvider.extractJti(claims),
                    jwtTokenProvider.getRemainingValidityMs(claims));   // 남은 시간만큼만 저장
        }
        log.info("[logout] memberId={}", memberId);
    }

    /*
     * comment.
     *  access token 재발급
     *  refresh token이 (1) 올바른 JWT이고 (2) Redis에 저장된 값과 같고 (3) 회원이 활성 상태일 때만 새 access token을 준다.
     *  refresh token 자체는 새로 발급하지 않는다(만료일 고정).
     * */
    @Override
    public TokenResult reissueAccessToken(String refreshToken) {
        // 쿠키가 없는 경우
        if (refreshToken == null) {
            throw AuthException.missingRefreshToken();
        }

        // 서명/만료/타입(REFRESH) 검증. access token을 refresh로 쓰는 것도 여기서 걸러짐
        Claims claims = jwtTokenProvider.resolveRefreshClaims(refreshToken);
        if (claims == null) {
            throw AuthException.invalidRefreshToken();
        }

        // Redis에 저장된 토큰과 비교: 로그아웃했거나 다른 기기에서 다시 로그인했다면 불일치
        Long memberId = jwtTokenProvider.extractMemberId(claims);
        if (!refreshTokenService.isValid(memberId, refreshToken)) {
            throw AuthException.invalidRefreshToken();
        }

        // 탈퇴한 회원이면 재발급 거절 (조회 실패도 같은 에러로 처리해 정보 노출 방지)
        Member member = memberRepository.findById(memberId)
                .filter(Member::isActive)
                .orElseThrow(AuthException::invalidRefreshToken);

        return new TokenResult(member.getId(),
                jwtTokenProvider.createAccessToken(member.getId()), member.getRole());
    }

    /*
     * comment.
     *  로그인 실패 공통 처리: 로그를 남기고 Redis 실패 카운터를 올린 뒤, 던질 예외를 돌려준다.
     *  (호출하는 쪽에서 throw 하도록 예외를 반환한다. 그래야 컴파일러가 "여기서 흐름이 끝난다"는 걸 안다)
     * */
    private AuthException loginFailed(String email, String clientIp, String reason, AuthException exception) {
        // 로그에는 이메일/IP를 마스킹해서 개인정보 노출을 막는다
        log.warn("[login] 실패 | email={} | ip={} | reason={}", maskEmail(email), maskIp(clientIp), reason);
        failureCountGuard.recordFailure(LOGIN_FAILURE_SCOPE, email,
                LOGIN_MAX_FAILURE_COUNT, LOGIN_FAILURE_WINDOW, LOGIN_LOCK_DURATION);
        return exception;
    }

    // 이메일 앞뒤 공백 제거 + 소문자 변환
    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    // 로그 출력용 마스킹: abcdef@x.com -> ab***@x.com
    private String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 2) return "***" + email.substring(Math.max(at, 0));
        return email.substring(0, 2) + "***" + email.substring(at);
    }

    // 로그 출력용 마스킹: 1.2.3.4 -> 1.2.3.*
    private String maskIp(String ip) {
        if (ip == null) return null;
        int lastDot = ip.lastIndexOf('.');
        return lastDot < 0 ? "***" : ip.substring(0, lastDot) + ".*";
    }

}
