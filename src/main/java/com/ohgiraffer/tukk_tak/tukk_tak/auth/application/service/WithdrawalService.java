package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.WithdrawalCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port.GoogleTokenVerifier;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.usecase.WithdrawalUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.event.MemberWithdrawnEvent;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.exception.AuthException;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.model.GoogleIdentity;
import com.ohgiraffer.tukk_tak.tukk_tak.global.exception.ResourceNotFoundException;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.FailureCountGuard;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.token.AccessTokenRevoker;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.token.RefreshTokenService;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Member;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Locale;

/*
 * comment.
 *  회원 탈퇴 서비스
 *  @Transactional을 클래스에 붙이지 않았다.
 *  탈퇴 저장은 레포지토리가 짧은 트랜잭션으로 처리하고, 그 뒤의 Redis 작업까지 DB 커넥션을 붙잡지 않게 하려는 것이다.
 *  데이터는 지우지 않고 상태만 WITHDRAWN으로 바꾼다. (같은 이메일로 다시 가입하면 같은 행을 되살린다)
 *  "개인정보 1년 보관 동의"는 동의하지 않으면 탈퇴 요청 자체가 400이라서, 탈퇴 시각(withdrawnAt)이 곧 동의 시각이다.
 * */

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawalService implements WithdrawalUsecase {

    // 비밀번호 확인 제한: 10분 안에 5번 틀리면 15분간 잠금 (비밀번호 변경과 같은 값, 따로 센다)
    private static final String FAILURE_SCOPE = "withdraw";
    private static final int MAX_FAILURE = 5;
    private static final Duration FAILURE_WINDOW = Duration.ofMinutes(10);
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final FailureCountGuard failureCountGuard;
    private final RefreshTokenService refreshTokenService;
    private final AccessTokenRevoker accessTokenRevoker;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void withdraw(WithdrawalCommand command) {

        String identifier = String.valueOf(command.memberId());

        // 1. 잠겨 있으면 비밀번호를 비교하지 않고 바로 거절 (잠금 중에는 맞는 비밀번호도 시도하지 못하게 한다)
        if (failureCountGuard.isLocked(FAILURE_SCOPE, identifier)) {
            throw AuthException.withdrawalLocked();
        }

        // 2. 회원 조회 (토큰은 유효한데 DB에 회원이 없는 경우는 글로벌 404로 처리)
        Member member = memberRepository.findById(command.memberId())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 회원입니다."));

        // 3. 본인 확인: 비밀번호가 있는 회원은 비밀번호로, 구글 회원은 새로 받은 구글 토큰으로 확인한다
        if (member.hasPassword()) {
            confirmByPassword(member, command.password(), identifier);
        } else {
            confirmByGoogle(member, command.credential());
        }

        // 4. 탈퇴 처리: 데이터는 지우지 않고 상태만 바꿔 저장
        member.withdraw();
        memberRepository.save(member);

        // 5. 이 회원의 refresh token을 지우고, 이미 발급된 access token도 모두 무효로 만든다 (지금 쓴 토큰 포함)
        //    (탈퇴를 저장한 뒤에 처리해야, 그 직후 다시 로그인하는 틈이 생기지 않는다)
        refreshTokenService.delete(member.getId());
        accessTokenRevoker.revokeAll(member.getId());

        // 6. 다른 도메인이 정리할 수 있게 탈퇴 이벤트를 알린다
        //    탈퇴는 이미 끝났으므로, 받는 쪽에서 실패해도 경고만 남기고 성공으로 응답한다
        try {
            eventPublisher.publishEvent(new MemberWithdrawnEvent(member.getId()));
        } catch (RuntimeException e) {
            log.warn("[withdrawal] 탈퇴 이벤트 처리 실패 | memberId={} type={}", member.getId(), e.getClass().getSimpleName());
        }

        // 7. 틀린 횟수를 지운다 (여기서 실패해도 탈퇴는 끝났으므로 경고만 남긴다)
        try {
            failureCountGuard.resetFailure(FAILURE_SCOPE, identifier);
        } catch (RuntimeException e) {
            log.warn("[withdrawal] 실패 횟수 초기화 실패 | type={}", e.getClass().getSimpleName());
        }

        log.info("[withdrawal] 탈퇴 완료 | memberId={}", member.getId());
    }

    // 비밀번호 확인: 틀리면 실패 횟수를 올리고, 이번에 한도에 걸려 잠겼다면 바로 잠김 응답
    private void confirmByPassword(Member member, String password, String identifier) {
        if (!StringUtils.hasText(password)) {
            throw AuthException.withdrawalConfirmRequired();
        }
        if (!passwordEncoder.matches(password, member.getPassword())) {
            failureCountGuard.recordFailure(FAILURE_SCOPE, identifier,
                    MAX_FAILURE, FAILURE_WINDOW, LOCK_DURATION);
            if (failureCountGuard.isLocked(FAILURE_SCOPE, identifier)) {
                throw AuthException.withdrawalLocked();
            }
            throw AuthException.currentPasswordMismatch();
        }
    }

    // 구글 재인증: 토큰 검증은 verifier가 하고(오류는 AuthException), 로그인한 계정과 같은 이메일인지만 비교한다
    private void confirmByGoogle(Member member, String credential) {
        if (!StringUtils.hasText(credential)) {
            throw AuthException.withdrawalConfirmRequired();
        }
        GoogleIdentity identity = googleTokenVerifier.verify(credential);
        String email = identity.email().trim().toLowerCase(Locale.ROOT);
        if (!email.equals(member.getEmail())) {
            throw AuthException.withdrawalGoogleMismatch();
        }
    }

}
