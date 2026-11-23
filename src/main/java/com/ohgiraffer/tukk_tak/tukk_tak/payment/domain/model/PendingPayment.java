package com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model;

import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;

/*
 * comment.
 *  결제 준비 단계에서 만든 임시 주문 (승인 단계에서 금액과 주문번호를 대조하는 기준)
 *  시작일과 종료일은 담지 않는다. 준비와 승인 사이에 다른 결제가 들어와 바뀔 수 있어서 승인 때 다시 계산한다.
 * */

public record PendingPayment(
        String orderId,          // 서버가 만든 주문번호
        PaymentMethod method,    // 결제 수단
        PlanName planName,       // 구매할 플랜
        int months,              // 구매할 개월 수
        int amount,              // 서버가 계산한 결제 금액
        String kakaoTid          // 카카오페이 결제 고유번호 (토스는 null)
) {
}
