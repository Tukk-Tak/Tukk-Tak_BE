package com.ohgiraffer.tukk_tak.tukk_tak.member.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.global.exception.ResourceNotFoundException;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.FailureCountGuard;
import com.ohgiraffer.tukk_tak.tukk_tak.global.util.BusinessNumberNormalizer;
import com.ohgiraffer.tukk_tak.tukk_tak.member.application.command.MemberBusinessVerifyCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.member.application.port.BusinessVerifier;
import com.ohgiraffer.tukk_tak.tukk_tak.member.application.result.BusinessCheckResult;
import com.ohgiraffer.tukk_tak.tukk_tak.member.application.result.MemberInfoResult;
import com.ohgiraffer.tukk_tak.tukk_tak.member.application.usecase.MemberBusinessUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.exception.MemberException;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.BusinessVerification;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Member;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.repository.BusinessVerificationRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

/*
 * comment.
 *  마이페이지 사업자 인증 서비스
 *  @Transactional을 클래스에 붙이지 않았다.
 *  국세청 호출은 느리고 외부 서버에 의존해서 트랜잭션 안에 넣지 않고, 저장은 레포지토리가 짧은 트랜잭션으로 처리한다.
 *  사업자번호 UNIQUE 위반은 트랜잭션이 끝난 여기서 잡아야 한다.
 * */

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberBusinessService implements MemberBusinessUsecase {

    // 서버 시간대와 상관없이 한국 시각으로 인증 시각을 남긴다
    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    // 회원당 사업자 인증 요청 제한: 10분 안에 10번을 넘으면 10분간 차단 (로그인 회원이라 IP가 아닌 회원 기준)
    private static final String BUSINESS_MEMBER_SCOPE = "business-verify-member";
    private static final int BUSINESS_MEMBER_MAX = 10;
    private static final Duration BUSINESS_MEMBER_WINDOW = Duration.ofMinutes(10);
    private static final Duration BUSINESS_MEMBER_LOCK = Duration.ofMinutes(10);

    private final MemberRepository memberRepository;
    private final BusinessVerificationRepository businessVerificationRepository;
    private final BusinessVerifier businessVerifier;
    private final FailureCountGuard failureCountGuard;

    @Override
    public MemberInfoResult.BusinessInfo verifyMyBusiness(MemberBusinessVerifyCommand command) {

        // 1. 회원 제한: 차단 중이면 바로 거절하고, 아니면 이번 요청을 1회로 센다
        //    중복 확인이나 국세청 호출보다 먼저 세어서, 번호를 바꿔가며 훑는 행위도 함께 막는다
        String limitKey = String.valueOf(command.memberId());
        if (failureCountGuard.isLocked(BUSINESS_MEMBER_SCOPE, limitKey)) {
            throw MemberException.businessVerifyLimitExceeded();
        }
        failureCountGuard.recordFailure(BUSINESS_MEMBER_SCOPE, limitKey,
                BUSINESS_MEMBER_MAX, BUSINESS_MEMBER_WINDOW, BUSINESS_MEMBER_LOCK);

        // 2. 회원 조회 (로그인한 회원이 DB에 없는 경우는 글로벌 404로 처리)
        Member member = memberRepository.findById(command.memberId())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 회원입니다."));

        // 3. 형식 검사는 요청 DTO에서 끝났으므로, 여기서는 숫자 10자리로 정규화만 한다
        String businessNumber = BusinessNumberNormalizer.normalize(command.businessNumber());

        // 4. 이미 내 현재 사업자인 번호면 거절 (국세청 호출 횟수도 아낀다)
        boolean isCurrent = businessVerificationRepository.findLatestByMemberId(member.getId())
                .map(b -> b.getBusinessNumber().equals(businessNumber))
                .orElse(false);
        if (isCurrent) {
            throw MemberException.businessAlreadyVerified();
        }

        // 5. 다른 회원이 인증한 번호면 거절 (내가 예전에 인증했던 번호로 돌아가는 건 통과)
        Optional<BusinessVerification> own = businessVerificationRepository
                .findByMemberIdAndBusinessNumber(member.getId(), businessNumber);
        if (own.isEmpty() && businessVerificationRepository.existsByBusinessNumber(businessNumber)) {
            throw MemberException.businessAlreadyUsed();
        }

        // 6. 국세청 진위확인: 대표자명은 입력받지 않고 회원 이름을 쓴다 (호출 실패나 타임아웃은 verifier가 503으로 던진다)
        BusinessCheckResult check = businessVerifier.verify(
                businessNumber, command.openDate(), member.getName());

        // 불일치: 신규 개업 직후에는 실제로 맞아도 불일치로 나올 수 있다 (안내 문구는 응답 메시지에 있음)
        if (!check.matched()) {
            throw MemberException.businessMismatch();
        }
        // 일치해도 폐업이면 거절 (휴업은 허용)
        if (check.closed()) {
            throw MemberException.businessClosed();
        }

        // 7. 저장: 통과한 뒤에만 저장해서, 실패하면 기존 인증은 그대로 유지된다
        //    예전 번호로 돌아가면 같은 기록의 인증 시각을 갱신하고, 새 번호면 기록을 추가한다 (예전 기록은 그대로 둔다)
        //    동시에 같은 번호로 인증해서 UNIQUE를 위반하면, 트랜잭션이 끝난 여기서 잡아 중복 에러로 바꾼다
        LocalDateTime now = LocalDateTime.now(SEOUL);
        BusinessVerification toSave = own
                .map(b -> b.renewed(command.openDate(), member.getName(), now))
                .orElseGet(() -> BusinessVerification.approvedByApi(
                        member.getId(), businessNumber, command.openDate(), member.getName(), now));
        BusinessVerification saved;
        try {
            saved = businessVerificationRepository.save(toSave);
        } catch (DataIntegrityViolationException e) {
            if (businessVerificationRepository.existsByBusinessNumber(businessNumber)) {
                throw MemberException.businessAlreadyUsed();
            }
            throw e;
        }

        // 사업자번호는 로그에 남기지 않는다
        log.info("[business] 마이페이지 인증 완료 | memberId={}", member.getId());
        return new MemberInfoResult.BusinessInfo(saved.getBusinessNumber(), saved.getOpenDate(),
                saved.getCeoName(), saved.getVerifiedAt());
    }


}
