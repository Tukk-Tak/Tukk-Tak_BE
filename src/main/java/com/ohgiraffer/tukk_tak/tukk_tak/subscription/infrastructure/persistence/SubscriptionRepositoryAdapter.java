package com.ohgiraffer.tukk_tak.tukk_tak.subscription.infrastructure.persistence;

import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.Subscription;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.SubscriptionStatus;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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
    @Transactional
    public boolean increaseAiUsage(Long subscriptionId, LocalDate cycleStart, int limit) {
        return jpaRepository.increaseAiUsage(subscriptionId, cycleStart, limit) > 0;
    }

    @Override
    @Transactional
    public void decreaseAiUsage(Long subscriptionId, LocalDate cycleStart) {
        jpaRepository.decreaseAiUsage(subscriptionId, cycleStart);
    }

}
