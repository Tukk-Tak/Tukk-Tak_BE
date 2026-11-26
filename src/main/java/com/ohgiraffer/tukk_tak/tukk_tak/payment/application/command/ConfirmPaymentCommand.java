package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.command;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentMethod;

/*
 * comment.
 *  결제 승인 요청 값
 *  토스는 paymentKey와 amount, 카카오는 pgToken을 쓰고 나머지는 null이다.
 * */

public record ConfirmPaymentCommand(
        Long memberId,
        PaymentMethod method,
        String orderId,
        String paymentKey,      // 토스 결제키
        Integer amount,         // 토스가 리다이렉트로 준 금액 (서버 주문 금액과 대조한다)
        String pgToken          // 카카오 승인 토큰
) {
}
