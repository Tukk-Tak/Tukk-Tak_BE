package com.ohgiraffer.tukk_tak.tukk_tak.payment.infrastructure.persistence;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentHistory;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

/*
 * comment.
 *  Spring Data JPA 인터페이스 (payment 테이블 조회/저장)
 * */

public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, Long> {

    // order_id UNIQUE 인덱스로 확인한다
    boolean existsByOrderId(String orderId);

    // order_id UNIQUE 인덱스로 한 건을 읽는다
    Optional<PaymentJpaEntity> findByOrderId(String orderId);

    // 결제와 구독을 한 번의 조인으로 읽어 결제 내역 한 줄(PaymentHistory)로 바로 만든다 (N+1 없음)
    // member_id + paid_at DESC 인덱스(idx_payment_member_paid)를 타서 별도 정렬 작업이 없다
    @Query(value = "select new com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentHistory("
            + "p.orderId, p.method, s.planName, p.months, p.amount, p.status, s.startDate, s.endDate, p.paidAt) "
            + "from PaymentJpaEntity p, SubscriptionJpaEntity s "
            + "where p.subscriptionId = s.id and p.memberId = :memberId "
            + "order by p.paidAt desc",
            countQuery = "select count(p) from PaymentJpaEntity p where p.memberId = :memberId")
    Page<PaymentHistory> findHistory(@Param("memberId") Long memberId, Pageable pageable);

    // 결제완료(PAID)인 결제만 한 번의 UPDATE로 취소 처리한다 (영향받은 행이 0이면 이미 취소됐거나 없는 결제)
    // 같은 웹훅이 동시에 두 번 와도 한쪽만 성공해서 중복 처리되지 않는다
    @Modifying(clearAutomatically = true)
    @Query("update PaymentJpaEntity p set p.status = :canceled, p.canceledAt = :canceledAt "
            + "where p.id = :id and p.status = :paid")
    int markCanceled(@Param("id") Long id, @Param("paid") PaymentStatus paid,
                     @Param("canceled") PaymentStatus canceled, @Param("canceledAt") LocalDateTime canceledAt);


}
