package com.ohgiraffer.tukk_tak.tukk_tak.member.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.global.security.FailureCountGuard;
import com.ohgiraffer.tukk_tak.tukk_tak.global.util.BusinessNumberNormalizer;
import com.ohgiraffer.tukk_tak.tukk_tak.global.util.SecureCodes;
import com.ohgiraffer.tukk_tak.tukk_tak.member.application.command.BusinessVerifyCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.member.application.port.BusinessTokenPort;
import com.ohgiraffer.tukk_tak.tukk_tak.member.application.port.BusinessVerifier;
import com.ohgiraffer.tukk_tak.tukk_tak.member.application.result.BusinessCheckResult;
import com.ohgiraffer.tukk_tak.tukk_tak.member.application.result.BusinessVerifyResult;
import com.ohgiraffer.tukk_tak.tukk_tak.member.application.usecase.BusinessVerifyUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.exception.MemberException;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.BusinessTokenInfo;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.repository.BusinessVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

/*
 * comment.
 *  사업자 인증 컨트롤러 (가입 화면용)
 *  가입 전에 호출하므로 URL은 /auth 아래에 둔다. (/auth/** 는 permitAll)
 * */

@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessVerifyService implements BusinessVerifyUsecase {

    // 사업자 확인 토큰 유효시간
    private static final Duration TOKEN_TTL = Duration.ofMinutes(30);

    // IP당 사업자 인증 요청 제한 (10분 10회 초과 시 10분 차단)
    private static final String BUSINESS_IP_SCOPE = "business-verify-ip";
    private static final int BUSINESS_IP_MAX = 10;
    private static final Duration BUSINESS_IP_WINDOW = Duration.ofMinutes(10);
    private static final Duration BUSINESS_IP_LOCK = Duration.ofMinutes(10);

    private final FailureCountGuard failureCountGuard;
    private final BusinessVerifier businessVerifier;
    private final BusinessTokenPort businessTokenPort;
    private final BusinessVerificationRepository businessVerificationRepository;

    @Override
    public BusinessVerifyResult verifyBusiness(BusinessVerifyCommand command) {

        // 1. IP 제한: 차단 중이면 바로 거절하고, 아니면 이번 요청을 1회로 센다
        //    중복 확인이나 국세청 호출보다 먼저 세어서, 번호를 바꿔가며 훑는 행위도 함께 막는다
        if (failureCountGuard.isLocked(BUSINESS_IP_SCOPE, command.clientIp())) {
            throw MemberException.businessVerifyLimitExceeded();
        }
        failureCountGuard.recordFailure(BUSINESS_IP_SCOPE, command.clientIp(),
                BUSINESS_IP_MAX, BUSINESS_IP_WINDOW, BUSINESS_IP_LOCK);

        // 2. 형식 검사는 요청 DTO에서 끝났으므로, 여기서는 숫자 10자리로 정규화만 한다
        String businessNumber = BusinessNumberNormalizer.normalize(command.businessNumber());

        // 3. 이미 다른 회원이 인증한 사업자번호면 거절 (국세청 호출 횟수도 아낀다)
        if (businessVerificationRepository.existsByBusinessNumber(businessNumber)) {
            throw MemberException.businessAlreadyUsed();
        }

        // 4. 국세청 진위확인 (호출 실패나 타임아웃은 verifier가 503으로 던진다)
        BusinessCheckResult check = businessVerifier.verify(
                businessNumber, command.openDate(), command.ceoName());

        // 불일치: 신규 개업 직후에는 실제로 맞아도 불일치로 나올 수 있다 (안내 문구는 응답 메시지에 있음)
        if (!check.matched()) {
            throw MemberException.businessMismatch();
        }
        // 일치해도 폐업이면 거절 (휴업은 허용)
        if (check.closed()) {
            throw MemberException.businessClosed();
        }

        // 5. 통과: 예측 불가능한 토큰을 만들어 사업자 정보와 함께 30분 저장
        //    가입 시 이 값으로 "가입 이름 = 대표자명"을 다시 비교한다
        String token = SecureCodes.urlSafeToken();
        businessTokenPort.saveToken(token,
                new BusinessTokenInfo(businessNumber, command.openDate(), command.ceoName()),
                TOKEN_TTL);

        // 사업자번호는 로그에 남기지 않는다
        log.info("[business] 인증 완료 | ip={}", command.clientIp());
        return new BusinessVerifyResult(token);
    }

}
