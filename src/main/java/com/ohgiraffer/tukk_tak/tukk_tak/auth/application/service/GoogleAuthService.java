package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.GoogleLoginCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.GoogleSignupCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port.BusinessTokenPort;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port.GoogleSignupTicketPort;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port.GoogleTokenVerifier;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.GoogleLoginResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.LoginResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.usecase.GoogleAuthUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.model.BusinessTokenInfo;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.model.GoogleIdentity;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.repository.BusinessVerificationRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.exception.AuthException;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.FailureCountGuard;
import com.ohgiraffer.tukk_tak.tukk_tak.global.util.PhoneNumberNormalizer;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.LoginType;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Member;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.MemberStatus;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Locale;
import java.util.UUID;

/*
 * comment.
 *  구글 로그인 서비스 (구글 ID 토큰으로 로그인하거나, 처음 온 계정이면 가입 티켓을 발급)
 *  클래스에 @Transactional을 걸지 않았다. DB는 읽기만 하고 나머지는 Redis와 토큰 검증이라 트랜잭션이 필요 없다.
 * */

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleAuthService implements GoogleAuthUsecase {

    // 구글 검증 실패 횟수를 세는 Redis 키의 구분자 (failure:google:{ip}, lock:google:{ip})
    private static final String GOOGLE_FAILURE_SCOPE = "google";
    // 이 횟수만큼 검증에 실패하면 해당 IP를 잠금
    private static final int GOOGLE_MAX_FAILURE_COUNT = 10;
    // 실패 횟수를 세는 기간 (첫 실패 후 10분이 지나면 카운터 리셋)
    private static final Duration GOOGLE_FAILURE_WINDOW = Duration.ofMinutes(10);
    // 잠금 유지 시간
    private static final Duration GOOGLE_LOCK_DURATION = Duration.ofMinutes(15);
    // 가입 티켓 유효 시간 (이메일 토큰, 사업자 토큰과 같은 30분)
    private static final Duration TICKET_TTL = Duration.ofMinutes(30);

    private final GoogleTokenVerifier googleTokenVerifier;
    private final GoogleSignupTicketPort googleSignupTicketPort;
    private final MemberRepository memberRepository;
    private final LoginTokenIssuer loginTokenIssuer;
    private final FailureCountGuard failureCountGuard;
    private final BusinessTokenPort businessTokenPort;
    private final BusinessVerificationRepository businessVerificationRepository;
    private final SignupRegistrar signupRegistrar;

    @Override
    public GoogleLoginResult googleLogin(GoogleLoginCommand command) {

        // 1. 검증 실패가 반복된 IP는 구글 검증 없이 바로 차단
        if (failureCountGuard.isLocked(GOOGLE_FAILURE_SCOPE, command.clientIp())) {
            throw AuthException.googleTooManyAttempts();
        }

        // 2. 구글 ID 토큰 검증 (서명, 발급 대상, 만료, 이메일 인증 여부). 실패하면 IP 기준 실패 횟수를 올린다
        GoogleIdentity identity;
        try {
            identity = googleTokenVerifier.verify(command.credential());
        } catch (AuthException e) {
            failureCountGuard.recordFailure(GOOGLE_FAILURE_SCOPE, command.clientIp(),
                    GOOGLE_MAX_FAILURE_COUNT, GOOGLE_FAILURE_WINDOW, GOOGLE_LOCK_DURATION);
            log.warn("[google] 토큰 검증 실패 | reason={}", e.getFailure());
            throw e;
        }
        failureCountGuard.resetFailure(GOOGLE_FAILURE_SCOPE, command.clientIp());

        // 3. 대소문자가 달라도 같은 계정으로 인식하도록 이메일 정규화 후 회원 조회
        String email = identity.email().trim().toLowerCase(Locale.ROOT);
        Member member = memberRepository.findByEmail(email).orElse(null);

        // 4. 처음 온 구글 계정: 가입 티켓을 발급해서 추가 정보 입력 화면으로 보낸다
        //    (UUID는 난수로 만들어져 추측할 수 없고, 30분 뒤 사라진다)
        if (member == null) {
            String ticket = UUID.randomUUID().toString();
            googleSignupTicketPort.saveTicket(ticket, new GoogleIdentity(email, identity.name()), TICKET_TTL);
            log.info("[google] 신규 계정, 가입 티켓 발급");
            return GoogleLoginResult.signupRequired(ticket, new GoogleIdentity(email, identity.name()));
        }

        // 5. 같은 이메일이 자체 가입으로 있으면 자동 연동하지 않고 거절 (login_type은 한 값만 가질 수 있다)
        if (member.getLoginType() != LoginType.GOOGLE) {
            throw AuthException.localAccountExists();
        }

        // 6. 탈퇴한 회원은 로그인 거절 (구글이 본인임을 확인했으므로 탈퇴 사실을 알려도 된다)
        if (member.getStatus() == MemberStatus.WITHDRAWN) {
            throw AuthException.withdrawnMember();
        }

        // 7. 로그인 성공: 구글 버튼에는 "로그인 상태 유지" 체크가 없어서 기본값(14일)으로 발급
        LoginResult login = loginTokenIssuer.issue(member, true);
        return GoogleLoginResult.loggedIn(login);
    }

    @Override
    public LoginResult googleSignup(GoogleSignupCommand command) {

        // 1. 가입 티켓으로 구글에서 인증된 이메일을 꺼낸다 (없거나 만료됐거나 이미 썼으면 구글 로그인을 다시)
        GoogleIdentity identity = googleSignupTicketPort.findTicket(command.signupTicket())
                .orElseThrow(AuthException::googleTicketInvalid);
        String email = identity.email();

        // 2. 사업자 확인 토큰이 있으면 꺼내서, 가입 이름이 대표자명과 같은지 다시 비교
        //    (프론트가 이름을 바꾸고도 "인증 완료"를 풀지 않은 경우를 서버에서 막는다)
        BusinessTokenInfo business = null;
        if (StringUtils.hasText(command.businessToken())) {
            business = businessTokenPort.findToken(command.businessToken())
                    .orElseThrow(AuthException::businessTokenInvalid);
            if (!business.ceoName().equals(command.name())) {
                throw AuthException.businessNameMismatch();
            }
        }

        // 3. 중복 확인 (안내용이고, 동시에 가입하는 경우는 5번의 UNIQUE 위반 변환이 마지막 방어선)
        //    구글 로그인과 가입 완료 사이에 같은 이메일로 다른 가입이 끝났을 수 있어서 이메일도 다시 확인
        if (memberRepository.existsByEmail(email)) {
            throw AuthException.emailAlreadyRegistered();
        }
        if (memberRepository.existsByNickname(command.nickname())) {
            throw AuthException.nicknameAlreadyUsed();
        }
        if (business != null && businessVerificationRepository.existsByBusinessNumber(business.businessNumber())) {
            throw AuthException.businessAlreadyUsed();
        }

        // 4. 회원 만들기: 구글 가입자는 비밀번호가 없고, 전화번호는 숫자만 남긴다
        //    동의는 체크박스 하나라서 개인정보 동의와 AI 사용 동의 컬럼에 같은 값을 넣는다
        Member member = Member.google(email, command.nickname(), command.name(),
                PhoneNumberNormalizer.normalize(command.phone()),
                PhoneNumberNormalizer.normalize(command.businessPhone()),
                StringUtils.hasText(command.address()) ? command.address().trim() : null,
                command.agreed(), command.agreed());

        // 5. 저장 (회원 + 사업자 인증 기록 + FREE 구독이 한 트랜잭션)
        //    같은 값으로 동시에 가입해서 UNIQUE를 위반하면, 트랜잭션이 끝난 여기서 잡아 중복 에러로 바꾼다
        Member saved;
        try {
            saved = signupRegistrar.register(member, business);
        } catch (DataIntegrityViolationException e) {
            throw translateDuplicate(email, command.nickname(), business, e);
        }

        // 6. 저장이 끝났으니 티켓과 사업자 토큰을 지워서 1회용으로 만든다
        //    (저장 전에 지우면 DB 오류 때 구글 로그인부터 다시 해야 한다. 삭제가 실패해도 가입은 성공으로 응답)
        try {
            googleSignupTicketPort.deleteTicket(command.signupTicket());
            if (business != null) {
                businessTokenPort.deleteToken(command.businessToken());
            }
        } catch (RuntimeException e) {
            log.warn("[google] 토큰 삭제 실패 | type={}", e.getClass().getSimpleName());
        }

        // 7. 비밀번호가 없어서 로그인 화면으로 보낼 수 없으니 바로 로그인 토큰을 발급 (14일)
        //    여기서 발급이 실패해도 가입은 끝난 상태라서, 구글 로그인으로 다시 들어오면 된다
        log.info("[google] 가입 완료");
        return loginTokenIssuer.issue(saved, true);
    }

    // UNIQUE 위반이 어떤 값 때문인지 다시 조회해서 중복 에러로 바꾼다 (5번 가입의 같은 이름 메서드와 같은 방식)
    // 셋 다 아니면 중복이 아닌 다른 무결성 오류이므로 원래 예외를 그대로 돌려준다
    private RuntimeException translateDuplicate(String email, String nickname,
                                                BusinessTokenInfo business, DataIntegrityViolationException e) {
        if (memberRepository.existsByEmail(email)) {
            return AuthException.emailAlreadyRegistered();
        }
        if (memberRepository.existsByNickname(nickname)) {
            return AuthException.nicknameAlreadyUsed();
        }
        if (business != null && businessVerificationRepository.existsByBusinessNumber(business.businessNumber())) {
            return AuthException.businessAlreadyUsed();
        }
        return e;
    }

}
