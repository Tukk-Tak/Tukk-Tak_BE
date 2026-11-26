package com.ohgiraffer.tukk_tak.tukk_tak.subscription.infrastructure.persistence;

import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.Subscription;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.SubscriptionStatus;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/*
 * comment.
 *  SubscriptionRepository 포트의 구현체
 *  도메인(Subscription)과 JPA 엔티티를 서로 변환한다.
 *  저장은 호출하는 SignupRegistrar가 트랜잭션을 시작하고, UPDATE 쿼리 메서드는 여기서 직접 시작한다.
 * */

@Repository
@RequiredArgsConstructor
public class SubscriptionRepositoryAdapter implements SubscriptionRepository {

    private final SubscriptionJpaRepository jpaRepository;

    @Override
    public Subscription save(Subscription subscription) {
        return jpaRepository.save(SubscriptionJpaEntity.fromDomain(subscription)).toDomain();
    }

    @Override
    public List<Subscription> findInUse(Long memberId, LocalDate today) {
        return jpaRepository.findInUse(memberId, SubscriptionStatus.ACTIVE, today).stream()
                .map(SubscriptionJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<LocalDate> findLatestEndDate(Long memberId, List<PlanName> planNames, LocalDate today) {
        // max()는 일치하는 행이 없으면 null이라 Optional로 감싼다
        return Optional.ofNullable(
                jpaRepository.findLatestEndDate(memberId, SubscriptionStatus.ACTIVE, planNames, today));
    }

    @Override
    @Transactional
    public boolean increaseAiUsage(Long subscriptionId, LocalDate cycleStart, int limit) {
        return jpaRepository.increaseAiUsage(subscriptionId, cycleStart, limit) > 0;
    }

    @Override
    @Transactional
    public void decreaseAiUsage(Long subscriptionId, LocalDate cycleStart) {
        jpaRepository.decreaseAiUsage(subscriptionId, cycleStart);
    }

    @Override
    @Transactional
    public void pauseLowerPlans(Long memberId, List<PlanName> lowerPlans, LocalDate fromDate, int days) {
        jpaRepository.pauseLowerPlans(memberId, lowerPlans.stream().map(Enum::name).toList(), fromDate, days);
    }

    @Override
    public List<Subscription> findUnexpired(Long memberId, LocalDate today) {
        return jpaRepository.findUnexpired(memberId, SubscriptionStatus.ACTIVE, today).stream()
                .map(SubscriptionJpaEntity::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public boolean cancel(Long subscriptionId, LocalDateTime canceledAt) {
        return jpaRepository.cancel(subscriptionId, SubscriptionStatus.ACTIVE, SubscriptionStatus.CANCELED,
                canceledAt) > 0;
    }

}
