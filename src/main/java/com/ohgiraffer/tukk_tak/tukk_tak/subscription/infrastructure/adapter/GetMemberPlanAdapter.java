package com.ohgiraffer.tukk_tak.tukk_tak.subscription.infrastructure.adapter;

import com.ohgiraffer.tukk_tak.tukk_tak.site.application.port.GetMemberPlanPort;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.SubscriptionStatus;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.infrastructure.persistence.SubscriptionJpaEntity;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.infrastructure.persistence.SubscriptionJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetMemberPlanAdapter implements GetMemberPlanPort {

    private final SubscriptionJpaRepository subscriptionJpaRepository;

    @Override
    public PlanName getPlan(Long memberId) {
        return subscriptionJpaRepository
                .findInUse(memberId, SubscriptionStatus.ACTIVE, LocalDate.now())
                .stream()
                .findFirst()
                .map(SubscriptionJpaEntity::getPlanName)
                .orElseGet(() -> {
                    log.warn("[get-member-plan] 이용 중인 구독 없음, FREE로 대체 | memberId={}", memberId);
                    return PlanName.FREE;
                });    }
}
