package com.ohgiraffer.tukk_tak.tukk_tak.subscription.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/*
 * comment.
 *  Spring Data JPA 인터페이스 (subscription 테이블 조회/저장)
 * */

public interface SubscriptionJpaRepository extends JpaRepository<SubscriptionJpaEntity, Long> {
}
