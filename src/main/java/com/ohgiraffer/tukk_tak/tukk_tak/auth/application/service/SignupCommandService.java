package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.*;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.policy.NicknamePolicy;
import com.ohgiraffer.tukk_tak.tukk_tak.member.application.port.BusinessTokenPort;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port.EmailCodePort;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port.EmailSender;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.*;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.usecase.SignupCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.exception.MemberException;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.BusinessTokenInfo;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.model.EmailPurpose;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.repository.BusinessVerificationRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.exception.AuthException;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.FailureCountGuard;
import com.ohgiraffer.tukk_tak.tukk_tak.global.util.EmailNormalizer;
import com.ohgiraffer.tukk_tak.tukk_tak.global.util.PhoneNumberNormalizer;
import com.ohgiraffer.tukk_tak.tukk_tak.global.util.SecureCodes;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.LoginType;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Member;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;

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

        // 2. 이메일 정규화 후 가입 불가한 이메일이면 거절 (화면의 "이미 가입된 이메일이에요 · 로그인하기")
        //    탈퇴한 자체 가입 회원은 같은 이메일로 복구 가입을 할 수 있으니 통과시킨다
        String email = EmailNormalizer.normalize(command.email());
        if (isUnavailableEmail(email)) {
            throw AuthException.emailAlreadyRegistered();
        }

        // 3. 재전송 대기: 60초 안에 다시 요청하면 거절 (원자적으로 한 번에 처리)
        if (!emailCodePort.acquireCooldown(EmailPurpose.SIGNUP, email, RESEND_COOLDOWN)) {
            throw AuthException.emailSendTooFast();
        }

        // 4. 인증번호를 만들어 먼저 저장하고, 새 번호를 받았으니 이전 번호로 틀린 횟수는 0으로 되돌린다
        String code = SecureCodes.sixDigitCode();
        emailCodePort.saveCode(EmailPurpose.SIGNUP, email, code, CODE_TTL);
        failureCountGuard.resetFailure(VERIFY_FAILURE_SCOPE, email);

        // 5. 메일 발송 (동기): 실패하면 방금 저장한 번호와 대기시간을 지워서,
        //    메일을 못 받았는데 60초를 기다려야 하는 상황이 생기지 않게 한다
        try {
            emailSender.sendVerificationCode(email, code);
        } catch (RuntimeException e) {
            emailCodePort.clear(EmailPurpose.SIGNUP, email);
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
        String savedCode = emailCodePort.findCode(EmailPurpose.SIGNUP, email);
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

        // 4. 성공: 쓴 번호는 즉시 지우고(재사용 방지), 실패 횟수를 초기화한 뒤 인증 토큰을 발급
        emailCodePort.deleteCode(EmailPurpose.SIGNUP, email);
        failureCountGuard.resetFailure(VERIFY_FAILURE_SCOPE, email);

        String token = SecureCodes.urlSafeToken();
        emailCodePort.saveToken(EmailPurpose.SIGNUP, token, email, TOKEN_TTL);

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
    public void signup(SignupCommand command) {

        // 1. 이메일 인증 토큰으로 인증된 이메일을 꺼낸다 (없거나 만료됐으면 다시 인증)
        String email = emailCodePort.findEmailByToken(EmailPurpose.SIGNUP, command.emailToken());
        if (email == null) {
            throw AuthException.emailTokenInvalid();
        }

        // 2. 사업자 확인 토큰이 있으면 꺼내서, 가입 이름이 대표자명과 같은지 다시 비교
        //    (프론트가 이름을 바꾸고도 "인증 완료"를 풀지 않은 경우를 서버에서 막는다)
        BusinessTokenInfo business = null;
        if (StringUtils.hasText(command.businessToken())) {
            business = businessTokenPort.findToken(command.businessToken())
                    .orElseThrow(MemberException::businessTokenInvalid);
            if (!business.ceoName().equals(command.name())) {
                throw MemberException.businessNameMismatch();
            }
        }

        // 3. 같은 이메일의 회원 확인: 탈퇴한 자체 가입 회원이면 새로 만들지 않고 되살린다
        //    (member id가 같아야 예전에 만든 사이트와 구독이 그대로 이어진다)
        Member existing = memberRepository.findByEmail(email).orElse(null);
        boolean restoring = existing != null && existing.isWithdrawn()
                && existing.getLoginType() == LoginType.LOCAL;
        if (existing != null && !restoring) {
            throw AuthException.emailAlreadyRegistered();
        }

        // 4. 닉네임, 사업자번호 중복 확인 (안내용이고, 동시에 가입하는 경우는 6번의 UNIQUE 위반 변환이 마지막 방어선)
        //    복구라면 본인이 쓰던 닉네임과 사업자번호는 중복으로 보지 않는다
        boolean ownNickname = restoring && command.nickname().equals(existing.getNickname());
        if (!ownNickname && memberRepository.existsByNickname(command.nickname())) {
            throw AuthException.nicknameAlreadyUsed();
        }
        boolean ownBusiness = restoring && business != null && businessVerificationRepository
                .existsByMemberIdAndBusinessNumber(existing.getId(), business.businessNumber());
        if (business != null && !ownBusiness
                && businessVerificationRepository.existsByBusinessNumber(business.businessNumber())) {
            throw MemberException.businessAlreadyUsed();
        }

        // 5. 저장할 값 준비: 비밀번호는 암호화하고 전화번호는 숫자만 남긴다
        String encodedPassword = passwordEncoder.encode(command.password());
        String phone = PhoneNumberNormalizer.normalize(command.phone());
        String businessPhone = PhoneNumberNormalizer.normalize(command.businessPhone());
        String address = StringUtils.hasText(command.address()) ? command.address().trim() : null;

        // 6. 저장: 복구면 기존 행을 덮어쓰고, 아니면 회원 + 사업자 인증 기록 + FREE 구독을 새로 만든다
        //    같은 값으로 동시에 가입해서 UNIQUE를 위반하면, 트랜잭션이 끝난 여기서 잡아 중복 에러로 바꾼다
        try {
            if (restoring) {
                existing.restore(encodedPassword, command.nickname(), command.name(), phone,
                        businessPhone, address,command.privacyAgreed(), command.aiUsageAgreed());
                signupRegistrar.restore(existing, ownBusiness ? null : business);
            } else {
                Member member = Member.local(email, encodedPassword, command.nickname(), command.name(),
                        phone, businessPhone, address, command.privacyAgreed(), command.aiUsageAgreed());
                signupRegistrar.register(member, business);
            }
        } catch (DataIntegrityViolationException e) {
            throw translateDuplicate(restoring ? null : email, ownNickname ? null : command.nickname(),
                    ownBusiness ? null : business, e);
        }

        // 7. 저장이 끝났으니 토큰을 지워서 1회용으로 만든다
        //    (저장 전에 지우면 DB 오류 때 인증을 처음부터 다시 해야 한다. 삭제가 실패해도 가입은 성공으로 응답)
        try {
            emailCodePort.deleteToken(EmailPurpose.SIGNUP, command.emailToken());
            if (business != null) {
                businessTokenPort.deleteToken(command.businessToken());
            }
        } catch (RuntimeException e) {
            log.warn("[signup] 토큰 삭제 실패 | type={}", e.getClass().getSimpleName());
        }

        log.info(restoring ? "[signup] 복구 가입 완료" : "[signup] 가입 완료");
    }

    // 가입 불가한 이메일인지 확인: 활성 회원이거나, 구글로 가입했다 탈퇴한 회원이면 true (구글은 구글 로그인으로 복구)
    private boolean isUnavailableEmail(String email) {
        return memberRepository.findByEmail(email)
                .map(member -> member.isActive() || member.getLoginType() != LoginType.LOCAL)
                .orElse(false);
    }

    // UNIQUE 위반이 어떤 값 때문인지 다시 조회해서 중복 에러로 바꾼다 (DB마다 제약 이름 메시지가 달라서 메시지는 분석하지 않는다)
    // 복구 가입에서는 본인 행이 이미 있으므로, 확인할 필요 없는 값은 null로 받아서 건너뛴다
    // 모두 아니면 중복이 아닌 다른 무결성 오류이므로 원래 예외를 그대로 돌려준다
    private RuntimeException translateDuplicate(String email, String nickname,
                                                BusinessTokenInfo business, DataIntegrityViolationException e) {
        if (email != null && memberRepository.existsByEmail(email)) {
            return AuthException.emailAlreadyRegistered();
        }
        if (nickname != null && memberRepository.existsByNickname(nickname)) {
            return AuthException.nicknameAlreadyUsed();
        }
        if (business != null && businessVerificationRepository.existsByBusinessNumber(business.businessNumber())) {
            return MemberException.businessAlreadyUsed();
        }
        return e;
    }

}
