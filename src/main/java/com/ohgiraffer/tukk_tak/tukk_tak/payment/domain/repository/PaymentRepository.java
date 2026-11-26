package com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.repository;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.Payment;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentHistoryPage;

import java.time.LocalDateTime;
import java.util.Optional;

/*
 * comment.
 *  결제 저장소 포트(인터페이스)
 * */

public interface PaymentRepository {

    // 결제 저장 (새로 만드는 저장만 지원)
    Payment save(Payment payment);

    // 이미 처리된 주문번호인지 (성공한 결제만 저장하므로 있으면 처리 완료)
    boolean existsByOrderId(String orderId);

    // 주문번호로 결제 한 건 조회 (없으면 empty)
    Optional<Payment> findByOrderId(String orderId);

    // 회원의 결제 내역을 최신순으로 한 페이지 가져온다 (page는 0부터)
    PaymentHistoryPage findHistory(Long memberId, int page, int size);

    // 결제완료 상태인 결제를 취소 상태로 바꾼다. 이미 취소됐거나 없으면 false
    boolean markCanceled(Long paymentId, LocalDateTime canceledAt);

}
