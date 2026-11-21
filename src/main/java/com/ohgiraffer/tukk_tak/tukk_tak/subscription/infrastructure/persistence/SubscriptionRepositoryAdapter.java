package com.ohgiraffer.tukk_tak.tukk_tak.subscription.infrastructure.persistence;

import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.Subscription;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/*
 * comment.
 *  SubscriptionRepository 포트의 구현체
 *  도메인(Subscription)과 JPA 엔티티를 서로 변환한다.
 *  트랜잭션은 호출하는 SignupRegistrar가 시작하므로 여기서는 붙이지 않는다.
 * */

@Repository
@RequiredArgsConstructor
public class SubscriptionRepositoryAdapter implements SubscriptionRepository {

    private final SubscriptionJpaRepository jpaRepository;

    @Override
    public Subscription save(Subscription subscription) {
        return jpaRepository.save(SubscriptionJpaEntity.fromDomain(subscription)).toDomain();
    }

}
