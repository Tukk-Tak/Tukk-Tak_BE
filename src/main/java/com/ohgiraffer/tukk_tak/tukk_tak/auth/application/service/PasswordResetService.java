package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.EmailSendCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.EmailVerifyCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.PasswordResetCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port.EmailCodePort;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port.EmailSender;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.EmailSendResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.PasswordResetVerifyResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.usecase.PasswordResetUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.exception.AuthException;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.model.EmailPurpose;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.FailureCountGuard;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.token.AccessTokenRevoker;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.token.RefreshTokenService;
import com.ohgiraffer.tukk_tak.tukk_tak.global.util.EmailNormalizer;
import com.ohgiraffer.tukk_tak.tukk_tak.global.util.SecureCodes;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Member;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

/*
 * comment.
 *  비밀번호 재설정 서비스
 *  -
 *  @Transactional을 클래스에 붙이지 않았다.
 *  메일 발송(SMTP)은 느리고 외부 서버에 의존해서, 트랜잭션 안에서 부르면 DB 커넥션을 오래 붙잡게 된다.
 *  비밀번호 저장은 레포지토리가 알아서 짧은 트랜잭션으로 처리한다.
 *  인증번호와 토큰은 회원가입과 같은 포트를 쓰되, 용도(PASSWORD_RESET)가 달라서 서로 섞이지 않는다.
 * */

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService implements PasswordResetUsecase {

    // 인증번호 유효시간 5분, 재전송 대기 60초 (회원가입 인증과 같은 값)
    private static final Duration CODE_TTL = Duration.ofMinutes(5);
    private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(60);

    // IP당 인증번호 요청 제한: 10분 안에 10번을 넘으면 10분간 차단 (회원가입 인증과 따로 센다)
    private static final String SEND_IP_SCOPE = "pw-reset-send-ip";
    private static final int SEND_IP_MAX = 10;
    private static final Duration SEND_IP_WINDOW = Duration.ofMinutes(10);
    private static final Duration SEND_IP_LOCK = Duration.ofMinutes(10);

    // 인증번호 검증 제한: 5분 안에 5번 틀리면 5분간 잠금 (새 번호를 받으면 0으로 되돌린다)
    private static final String VERIFY_FAILURE_SCOPE = "pw-reset-verify";
    private static final int VERIFY_MAX_FAILURE = 5;
    private static final Duration VERIFY_FAILURE_WINDOW = Duration.ofMinutes(5);
    private static final Duration VERIFY_LOCK = Duration.ofMinutes(5);

    // 재설정 토큰 유효시간 10분 (새 비밀번호를 입력하는 동안만 유지)
    private static final Duration TOKEN_TTL = Duration.ofMinutes(10);

    private final MemberRepository memberRepository;
    private final EmailCodePort emailCodePort;
    private final EmailSender emailSender;
    private final FailureCountGuard failureCountGuard;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final AccessTokenRevoker accessTokenRevoker;

    @Override
    public EmailSendResult sendResetCode(EmailSendCommand command) {

        // 1. IP 제한: 차단 중이면 바로 거절하고, 아니면 이번 요청을 1회로 센다
        //    회원 확인보다 먼저 세어서, 이메일을 바꿔가며 가입 여부를 훑는 행위도 함께 막는다
        if (failureCountGuard.isLocked(SEND_IP_SCOPE, command.clientIp())) {
            throw AuthException.emailSendLimitExceeded();
        }
        failureCountGuard.recordFailure(SEND_IP_SCOPE, command.clientIp(),
                SEND_IP_MAX, SEND_IP_WINDOW, SEND_IP_LOCK);

        // 2. 재설정할 수 있는 회원인지 확인: 가입 안 됨, 탈퇴, 구글 회원(비밀번호 없음)은 각각 다른 안내로 거절
        //    (회원가입 API가 이미 가입 여부를 알려주고 있어서, 여기서 숨겨도 이득이 없고 사용자만 헷갈린다)
        String email = EmailNormalizer.normalize(command.email());
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(AuthException::emailNotRegistered);
        if (member.isWithdrawn()) {
            throw AuthException.passwordResetWithdrawn();
        }
        if (!member.hasPassword()) {
            throw AuthException.passwordResetSocialAccount();
        }

        // 3. 재전송 대기: 60초 안에 다시 요청하면 거절 (원자적으로 한 번에 처리)
        if (!emailCodePort.acquireCooldown(EmailPurpose.PASSWORD_RESET, email, RESEND_COOLDOWN)) {
            throw AuthException.emailSendTooFast();
        }

        // 4. 인증번호를 만들어 먼저 저장하고, 새 번호를 받았으니 이전 번호로 틀린 횟수는 0으로 되돌린다
        String code = SecureCodes.sixDigitCode();
        emailCodePort.saveCode(EmailPurpose.PASSWORD_RESET, email, code, CODE_TTL);
        failureCountGuard.resetFailure(VERIFY_FAILURE_SCOPE, email);

        // 5. 메일 발송 (동기): 실패하면 방금 저장한 번호와 대기시간을 지워서,
        //    메일을 못 받았는데 60초를 기다려야 하는 상황이 생기지 않게 한다
        try {
            emailSender.sendPasswordResetCode(email, code);
        } catch (RuntimeException e) {
            emailCodePort.clear(EmailPurpose.PASSWORD_RESET, email);
            throw e;
        }

        log.info("[password-reset] 인증번호 발송 | ip={}", command.clientIp());
        return new EmailSendResult(CODE_TTL.getSeconds(), RESEND_COOLDOWN.getSeconds());
    }

    @Override
    public PasswordResetVerifyResult verifyResetCode(EmailVerifyCommand command) {

        String email = EmailNormalizer.normalize(command.email());

        // 1. 잠겨 있으면 번호를 비교하지 않고 바로 거절
        if (failureCountGuard.isLocked(VERIFY_FAILURE_SCOPE, email)) {
            throw AuthException.emailVerifyLocked();
        }

        // 2. 저장된 번호가 없으면 만료됐거나 요청한 적이 없는 것 (실패 횟수는 올리지 않는다)
        String savedCode = emailCodePort.findCode(EmailPurpose.PASSWORD_RESET, email);
        if (savedCode == null) {
            throw AuthException.emailCodeExpired();
        }

        // 3. 번호 비교: 틀리면 실패 횟수를 올리고, 이번에 한도에 걸려 잠겼다면 바로 잠김 응답
        if (!SecureCodes.matches(savedCode, command.code())) {
            failureCountGuard.recordFailure(VERIFY_FAILURE_SCOPE, email,
                    VERIFY_MAX_FAILURE, VERIFY_FAILURE_WINDOW, VERIFY_LOCK);
            if (failureCountGuard.isLocked(VERIFY_FAILURE_SCOPE, email)) {
                throw AuthException.emailVerifyLocked();
            }
            throw AuthException.emailCodeMismatch();
        }

        // 4. 성공: 쓴 번호는 즉시 지우고(재사용 방지), 실패 횟수를 초기화한 뒤 재설정 토큰을 발급
        emailCodePort.deleteCode(EmailPurpose.PASSWORD_RESET, email);
        failureCountGuard.resetFailure(VERIFY_FAILURE_SCOPE, email);

        String token = SecureCodes.urlSafeToken();
        emailCodePort.saveToken(EmailPurpose.PASSWORD_RESET, token, email, TOKEN_TTL);

        log.info("[password-reset] 인증 완료");
        return new PasswordResetVerifyResult(token);
    }

    @Override
    public void resetPassword(PasswordResetCommand command) {

        // 1. 재설정 토큰으로 인증된 이메일을 꺼낸다 (없거나 만료됐으면 인증번호부터 다시)
        String email = emailCodePort.findEmailByToken(EmailPurpose.PASSWORD_RESET, command.resetToken());
        if (email == null) {
            throw AuthException.passwordResetTokenInvalid();
        }

        // 2. 토큰을 받은 뒤 회원 상태가 바뀌었을 수 있어서 다시 확인 (그 사이 탈퇴했거나 구글 회원이면 거절)
        Member member = memberRepository.findByEmail(email)
                .filter(found -> found.isActive() && found.hasPassword())
                .orElseThrow(AuthException::passwordResetTokenInvalid);

        // 3. 현재 비밀번호와 같으면 거절 (바뀐 게 없는데 세션만 끊기는 일을 막는다)
        if (passwordEncoder.matches(command.newPassword(), member.getPassword())) {
            throw AuthException.passwordSameAsCurrent();
        }

        // 4. 새 비밀번호를 암호화해서 저장
        member.changePassword(passwordEncoder.encode(command.newPassword()));
        memberRepository.save(member);

        // 5. 이 회원의 refresh token을 지우고, 이미 발급된 access token도 모두 무효로 만든다
        //    (비밀번호를 저장한 뒤에 처리해야, 그 직후 옛 비밀번호로 다시 로그인하는 틈이 생기지 않는다)
        refreshTokenService.delete(member.getId());
        accessTokenRevoker.revokeAll(member.getId());

        // 6. 쓴 토큰을 지우고(재사용 방지), 로그인 잠금을 푼다 (메일로 본인 확인을 마쳤으니 잠겨 있어도 풀어준다)
        //    여기서 실패해도 비밀번호는 이미 바뀌었으므로, 가입처럼 경고만 남기고 성공으로 응답한다
        try {
            emailCodePort.deleteToken(EmailPurpose.PASSWORD_RESET, command.resetToken());
            failureCountGuard.clear(AuthCommandService.LOGIN_FAILURE_SCOPE, email);
        } catch (RuntimeException e) {
            log.warn("[password-reset] 토큰 삭제 또는 잠금 해제 실패 | type={}", e.getClass().getSimpleName());
        }

        log.info("[password-reset] 재설정 완료 | memberId={}", member.getId());
    }

}
