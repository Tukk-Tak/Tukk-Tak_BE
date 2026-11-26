package com.ohgiraffer.tukk_tak.tukk_tak.payment.infrastructure.persistence;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/*
 * comment.
 *  Spring Data JPA 인터페이스 (payment 테이블 조회/저장)
 * */

public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, Long> {

    // order_id UNIQUE 인덱스로 확인한다
    boolean existsByOrderId(String orderId);

    // 결제와 구독을 한 번의 조인으로 읽어 결제 내역 한 줄(PaymentHistory)로 바로 만든다 (N+1 없음)
    // member_id + paid_at DESC 인덱스(idx_payment_member_paid)를 타서 별도 정렬 작업이 없다
    @Query(value = "select new com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentHistory("
            + "p.orderId, p.method, s.planName, p.months, p.amount, s.startDate, s.endDate, p.paidAt) "
            + "from PaymentJpaEntity p, SubscriptionJpaEntity s "
            + "where p.subscriptionId = s.id and p.memberId = :memberId "
            + "order by p.paidAt desc",
            countQuery = "select count(p) from PaymentJpaEntity p where p.memberId = :memberId")
    Page<PaymentHistory> findHistory(@Param("memberId") Long memberId, Pageable pageable);

}
