package com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.repository;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.Payment;

/*
 * comment.
 *  결제 저장소 포트(인터페이스)
 * */

public interface PaymentRepository {

    // 결제 저장 (새로 만드는 저장만 지원)
    Payment save(Payment payment);

    // 이미 처리된 주문번호인지 (성공한 결제만 저장하므로 있으면 처리 완료)
    boolean existsByOrderId(String orderId);

}
