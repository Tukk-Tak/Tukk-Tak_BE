package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.BusinessTokenInfo;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.BusinessVerification;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.repository.BusinessVerificationRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Member;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.repository.MemberRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.Subscription;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/*
 * comment.
 *  가입 저장 담당 (회원 + 사업자 인증 기록 + FREE 구독을 한 트랜잭션으로 저장)
 *  SignupCommandService 안에서 @Transactional 메서드를 부르면 트랜잭션이 적용되지 않아서(자기 호출) 따로 뺐다.
 *  하나라도 실패하면 전부 되돌려져서, 회원만 생기고 구독이 없는 상태가 남지 않는다.
 * */

@Component
@RequiredArgsConstructor
public class SignupRegistrar {

    // 서버 시간대와 상관없이 한국 날짜로 구독 시작일을 정한다
    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private final MemberRepository memberRepository;
    private final BusinessVerificationRepository businessVerificationRepository;
    private final SubscriptionRepository subscriptionRepository;

    // UNIQUE 위반은 여기서 잡지 않는다 (잡으면 rollback-only가 남아서 호출하는 쪽이 트랜잭션 종료 뒤에 잡는다)
    @Transactional
    public Member register(Member member, BusinessTokenInfo business) {

        // 1. 회원 저장 (아래 두 건에 member id가 필요해서 가장 먼저)
        Member saved = memberRepository.save(member);

        // 2. 사업자 확인 토큰이 있었다면 인증 기록 저장 (APPROVED만 저장)
        if (business != null) {
            businessVerificationRepository.save(BusinessVerification.approvedByApi(
                    saved.getId(), business.businessNumber(), business.openDate(),
                    business.ceoName(), LocalDateTime.now(SEOUL)));
        }

        // 3. FREE 구독 자동 생성
        subscriptionRepository.save(Subscription.free(saved.getId(), LocalDate.now(SEOUL)));

        return saved;
    }

    // 탈퇴한 회원 복구: 같은 행을 덮어써서 예전 사이트와 구독을 잇고, FREE 구독은 이미 있으니 만들지 않는다
    // business가 null이면 사업자 기록은 건드리지 않는다 (인증을 안 했거나, 본인 기록에 이미 있는 번호)
    @Transactional
    public Member restore(Member member, BusinessTokenInfo business) {

        // 1. 회원 저장 (member id가 있어서 INSERT가 아니라 UPDATE로 처리됨)
        Member saved = memberRepository.save(member);

        // 2. 새 사업자번호로 인증했다면 인증 기록만 추가 (예전 기록은 그대로 둔다)
        if (business != null) {
            businessVerificationRepository.save(BusinessVerification.approvedByApi(
                    saved.getId(), business.businessNumber(), business.openDate(),
                    business.ceoName(), LocalDateTime.now(SEOUL)));
        }

        return saved;
    }

}
