package com.ohgiraffer.tukk_tak.tukk_tak.payment.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/*
 * comment.
 *  Spring Data JPA 인터페이스 (payment 테이블 조회/저장)
 * */

public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, Long> {

    // order_id UNIQUE 인덱스로 확인한다
    boolean existsByOrderId(String orderId);

}
