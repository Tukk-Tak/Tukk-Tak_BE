package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.EmailSendCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.EmailVerifyCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.NicknameCheckCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.policy.NicknamePolicy;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port.EmailCodePort;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port.EmailSender;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.EmailSendResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.EmailVerifyResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.NicknameCheckResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.usecase.SignupCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.exception.AuthException;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.FailureCountGuard;
import com.ohgiraffer.tukk_tak.tukk_tak.global.util.EmailNormalizer;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;

/*
 * comment.
 *  회원가입 서비스
 *  -
 *  @Transactional을 클래스에 붙이지 않았다.
 *  메일 발송(SMTP)은 느리고 외부 서버에 의존하는데, 트랜잭션 안에서 부르면 그 시간 동안 DB 커넥션을 붙잡고 있게 된다.
 *  가입 여부 조회(existsByEmail)는 레포지토리가 알아서 짧은 읽기 트랜잭션으로 처리한다.
 * */

@Slf4j
@Service
@RequiredArgsConstructor
public class SignupCommandService implements SignupCommandUsecase {

    // 인증번호는 예측할 수 없어야 하므로 SecureRandom을 쓴다 (Random은 예측 가능성이 있다)
    private static final SecureRandom RANDOM = new SecureRandom();

    // 인증번호 유효시간 5분, 재전송 대기 60초 (화면의 04:32 타이머와 같은 값)
    private static final Duration CODE_TTL = Duration.ofMinutes(5);
    private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(60);

    // IP당 인증번호 요청 제한: 10분 안에 10번을 넘으면 10분간 차단
    private static final String SEND_IP_SCOPE = "email-send-ip";
    private static final int SEND_IP_MAX = 10;
    private static final Duration SEND_IP_WINDOW = Duration.ofMinutes(10);
    private static final Duration SEND_IP_LOCK = Duration.ofMinutes(10);

    // 인증번호 검증 실패 카운터의 구분자 (2번 API에서 사용, 새 번호를 받으면 0으로 되돌린다)
    private static final String VERIFY_FAILURE_SCOPE = "email-verify";

    // 인증번호 검증 제한: 5분 안에 5번 틀리면 5분간 잠금 (6자리 번호를 무한정 대입하는 공격 방지)
    private static final int VERIFY_MAX_FAILURE = 5;
    private static final Duration VERIFY_FAILURE_WINDOW = Duration.ofMinutes(5);
    private static final Duration VERIFY_LOCK = Duration.ofMinutes(5);

    // 이메일 인증 토큰 유효시간 30분 (가입 화면에서 입력하는 동안 유지)
    private static final Duration TOKEN_TTL = Duration.ofMinutes(30);

    // IP당 닉네임 확인 제한: 1분 안에 30번을 넘으면 1분간 차단 (닉네임을 대량으로 훑어보는 것 방지)
    private static final String NICKNAME_IP_SCOPE = "nickname-check-ip";
    private static final int NICKNAME_IP_MAX = 30;
    private static final Duration NICKNAME_IP_WINDOW = Duration.ofMinutes(1);
    private static final Duration NICKNAME_IP_LOCK = Duration.ofMinutes(1);

    private final MemberRepository memberRepository;
    private final EmailCodePort emailCodePort;
    private final EmailSender emailSender;
    private final FailureCountGuard failureCountGuard;

    @Override
    public EmailSendResult sendEmailCode(EmailSendCommand command) {

        // 1. IP 제한: 차단 중이면 바로 거절하고, 아니면 이번 요청을 1회로 센다
        //    가입 여부 확인보다 먼저 세어서, 이메일을 바꿔가며 가입 여부를 훑는 행위도 함께 막는다
        if (failureCountGuard.isLocked(SEND_IP_SCOPE, command.clientIp())) {
            throw AuthException.emailSendLimitExceeded();
        }
        failureCountGuard.recordFailure(SEND_IP_SCOPE, command.clientIp(),
                SEND_IP_MAX, SEND_IP_WINDOW, SEND_IP_LOCK);

        // 2. 이메일 정규화 후 이미 가입된 이메일이면 거절 (화면의 "이미 가입된 이메일이에요 · 로그인하기")
        String email = EmailNormalizer.normalize(command.email());
        if (memberRepository.existsByEmail(email)) {
            throw AuthException.emailAlreadyRegistered();
        }

        // 3. 재전송 대기: 60초 안에 다시 요청하면 거절 (원자적으로 한 번에 처리)
        if (!emailCodePort.acquireCooldown(email, RESEND_COOLDOWN)) {
            throw AuthException.emailSendTooFast();
        }

        // 4. 인증번호를 만들어 먼저 저장하고, 새 번호를 받았으니 이전 번호로 틀린 횟수는 0으로 되돌린다
        String code = generateCode();
        emailCodePort.saveCode(email, code, CODE_TTL);
        failureCountGuard.resetFailure(VERIFY_FAILURE_SCOPE, email);

        // 5. 메일 발송 (동기): 실패하면 방금 저장한 번호와 대기시간을 지워서,
        //    메일을 못 받았는데 60초를 기다려야 하는 상황이 생기지 않게 한다
        try {
            emailSender.sendVerificationCode(email, code);
        } catch (RuntimeException e) {
            emailCodePort.clear(email);
            throw e;
        }

        log.info("[email] 인증번호 발송 | ip={}", command.clientIp());
        return new EmailSendResult(CODE_TTL.getSeconds(), RESEND_COOLDOWN.getSeconds());
    }

    @Override
    public EmailVerifyResult verifyEmailCode(EmailVerifyCommand command) {

        String email = EmailNormalizer.normalize(command.email());

        // 1. 잠겨 있으면 번호를 비교하지 않고 바로 거절
        if (failureCountGuard.isLocked(VERIFY_FAILURE_SCOPE, email)) {
            throw AuthException.emailVerifyLocked();
        }

        // 2. 저장된 번호가 없으면 만료됐거나 요청한 적이 없는 것 (실패 횟수는 올리지 않는다)
        String savedCode = emailCodePort.findCode(email);
        if (savedCode == null) {
            throw AuthException.emailCodeExpired();
        }

        // 3. 번호 비교: 틀리면 실패 횟수를 올리고, 이번에 한도에 걸려 잠겼다면 바로 잠김 응답
        if (!isSameCode(savedCode, command.code())) {
            failureCountGuard.recordFailure(VERIFY_FAILURE_SCOPE, email,
                    VERIFY_MAX_FAILURE, VERIFY_FAILURE_WINDOW, VERIFY_LOCK);
            if (failureCountGuard.isLocked(VERIFY_FAILURE_SCOPE, email)) {
                throw AuthException.emailVerifyLocked();
            }
            throw AuthException.emailCodeMismatch();
        }

        // 4. 성공: 쓴 번호는 즉시 지우고(재사용 방지), 실패 횟수를 초기화한 뒤 인증 토큰을 발급
        emailCodePort.deleteCode(email);
        failureCountGuard.resetFailure(VERIFY_FAILURE_SCOPE, email);

        String token = generateToken();
        emailCodePort.saveToken(token, email, TOKEN_TTL);

        log.info("[email] 인증 완료");
        return new EmailVerifyResult(token);
    }

    @Override
    public NicknameCheckResult checkNickname(NicknameCheckCommand command) {

        // 1. IP 제한: 차단 중이면 거절하고, 아니면 이번 요청을 1회로 센다
        if (failureCountGuard.isLocked(NICKNAME_IP_SCOPE, command.clientIp())) {
            throw AuthException.nicknameCheckLimitExceeded();
        }
        failureCountGuard.recordFailure(NICKNAME_IP_SCOPE, command.clientIp(),
                NICKNAME_IP_MAX, NICKNAME_IP_WINDOW, NICKNAME_IP_LOCK);

        // 2. 닉네임 규칙 검사 (틀리면 400). 앞뒤 공백을 자르지 않아서, 확인한 값이 곧 저장될 값이 된다
        NicknamePolicy.validate(command.nickname());

        // 3. 중복 확인: UNIQUE 인덱스를 타는 조회 한 번이라 가볍다 (결과는 캐시하지 않는다)
        boolean exists = memberRepository.existsByNickname(command.nickname());
        return new NicknameCheckResult(!exists);
    }

    // 두 번호를 비교할 때 일치하는 자릿수에 따라 걸리는 시간이 달라지지 않도록 MessageDigest.isEqual을 쓴다
    private boolean isSameCode(String saved, String input) {
        return MessageDigest.isEqual(
                saved.getBytes(StandardCharsets.UTF_8),
                input.getBytes(StandardCharsets.UTF_8));
    }

    // 예측할 수 없는 토큰: SecureRandom 32바이트를 URL에 안전한 문자열로 변환 (43자)
    private String generateToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // 6자리 숫자 (앞이 0이어도 6자리를 유지: 000123)
    private String generateCode() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }

}
