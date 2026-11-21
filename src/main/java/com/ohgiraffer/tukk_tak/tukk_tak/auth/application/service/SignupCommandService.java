package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.*;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.policy.NicknamePolicy;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port.BusinessTokenPort;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port.BusinessVerifier;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port.EmailCodePort;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port.EmailSender;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.*;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.usecase.SignupCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.model.BusinessTokenInfo;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.repository.BusinessVerificationRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.exception.AuthException;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.FailureCountGuard;
import com.ohgiraffer.tukk_tak.tukk_tak.global.util.BusinessNumberNormalizer;
import com.ohgiraffer.tukk_tak.tukk_tak.global.util.EmailNormalizer;
import com.ohgiraffer.tukk_tak.tukk_tak.global.util.PhoneNumberNormalizer;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Member;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    // IP당 사업자 인증 요청 제한: 10분 안에 10번을 넘으면 10분간 차단
    private static final String BUSINESS_IP_SCOPE = "business-verify-ip";
    private static final int BUSINESS_IP_MAX = 10;
    private static final Duration BUSINESS_IP_WINDOW = Duration.ofMinutes(10);
    private static final Duration BUSINESS_IP_LOCK = Duration.ofMinutes(10);

    private final MemberRepository memberRepository;
    private final EmailCodePort emailCodePort;
    private final EmailSender emailSender;
    private final FailureCountGuard failureCountGuard;
    private final BusinessVerifier businessVerifier;
    private final BusinessTokenPort businessTokenPort;
    private final BusinessVerificationRepository businessVerificationRepository;
    private final SignupRegistrar signupRegistrar;
    private final PasswordEncoder passwordEncoder;

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

    @Override
    public BusinessVerifyResult verifyBusiness(BusinessVerifyCommand command) {

        // 1. IP 제한: 차단 중이면 바로 거절하고, 아니면 이번 요청을 1회로 센다
        //    중복 확인이나 국세청 호출보다 먼저 세어서, 번호를 바꿔가며 훑는 행위도 함께 막는다
        if (failureCountGuard.isLocked(BUSINESS_IP_SCOPE, command.clientIp())) {
            throw AuthException.businessVerifyLimitExceeded();
        }
        failureCountGuard.recordFailure(BUSINESS_IP_SCOPE, command.clientIp(),
                BUSINESS_IP_MAX, BUSINESS_IP_WINDOW, BUSINESS_IP_LOCK);

        // 2. 형식 검사는 요청 DTO에서 끝났으므로, 여기서는 숫자 10자리로 정규화만 한다
        String businessNumber = BusinessNumberNormalizer.normalize(command.businessNumber());

        // 3. 이미 다른 회원이 인증한 사업자번호면 거절 (국세청 호출 횟수도 아낀다)
        if (businessVerificationRepository.existsByBusinessNumber(businessNumber)) {
            throw AuthException.businessAlreadyUsed();
        }

        // 4. 국세청 진위확인 (호출 실패나 타임아웃은 verifier가 503으로 던진다)
        BusinessCheckResult check = businessVerifier.verify(
                businessNumber, command.openDate(), command.ceoName());

        // 불일치: 신규 개업 직후에는 실제로 맞아도 불일치로 나올 수 있다 (안내 문구는 응답 메시지에 있음)
        if (!check.matched()) {
            throw AuthException.businessMismatch();
        }
        // 일치해도 폐업이면 거절 (휴업은 허용)
        if (check.closed()) {
            throw AuthException.businessClosed();
        }

        // 5. 통과: 예측 불가능한 토큰을 만들어 사업자 정보와 함께 30분 저장
        //    5번 가입에서 이 값으로 "가입 이름 = 대표자명"을 다시 비교한다
        //    (generateToken과 TOKEN_TTL은 이메일 인증에서 쓰던 것을 그대로 재사용)
        String token = generateToken();
        businessTokenPort.saveToken(token,
                new BusinessTokenInfo(businessNumber, command.openDate(), command.ceoName()),
                TOKEN_TTL);

        // 사업자번호는 로그에 남기지 않는다
        log.info("[business] 인증 완료 | ip={}", command.clientIp());
        return new BusinessVerifyResult(token);
    }

    @Override
    public void signup(SignupCommand command) {

        // 1. 이메일 인증 토큰으로 인증된 이메일을 꺼낸다 (없거나 만료됐으면 다시 인증)
        String email = emailCodePort.findEmailByToken(command.emailToken());
        if (email == null) {
            throw AuthException.emailTokenInvalid();
        }

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
        if (memberRepository.existsByEmail(email)) {
            throw AuthException.emailAlreadyRegistered();
        }
        if (memberRepository.existsByNickname(command.nickname())) {
            throw AuthException.nicknameAlreadyUsed();
        }
        if (business != null && businessVerificationRepository.existsByBusinessNumber(business.businessNumber())) {
            throw AuthException.businessAlreadyUsed();
        }

        // 4. 회원 만들기: 비밀번호는 암호화하고 전화번호는 숫자만 남긴다
        Member member = Member.local(email, passwordEncoder.encode(command.password()),
                command.nickname(), command.name(),
                PhoneNumberNormalizer.normalize(command.phone()),
                PhoneNumberNormalizer.normalize(command.businessPhone()),
                StringUtils.hasText(command.address()) ? command.address().trim() : null,
                command.agreed(), command.agreed());

        // 5. 저장 (회원 + 사업자 인증 기록 + FREE 구독이 한 트랜잭션)
        //    같은 값으로 동시에 가입해서 UNIQUE를 위반하면, 트랜잭션이 끝난 여기서 잡아 중복 에러로 바꾼다
        try {
            signupRegistrar.register(member, business);
        } catch (DataIntegrityViolationException e) {
            throw translateDuplicate(email, command.nickname(), business, e);
        }

        // 6. 저장이 끝났으니 토큰을 지워서 1회용으로 만든다
        //    (저장 전에 지우면 DB 오류 때 인증을 처음부터 다시 해야 한다. 삭제가 실패해도 가입은 성공으로 응답)
        try {
            emailCodePort.deleteToken(command.emailToken());
            if (business != null) {
                businessTokenPort.deleteToken(command.businessToken());
            }
        } catch (RuntimeException e) {
            log.warn("[signup] 토큰 삭제 실패 | type={}", e.getClass().getSimpleName());
        }

        log.info("[signup] 가입 완료");
    }

    // UNIQUE 위반이 어떤 값 때문인지 다시 조회해서 중복 에러로 바꾼다 (DB마다 제약 이름 메시지가 달라서 메시지는 분석하지 않는다)
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
