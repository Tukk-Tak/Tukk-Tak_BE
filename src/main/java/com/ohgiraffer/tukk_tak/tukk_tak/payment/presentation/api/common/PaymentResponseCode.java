package com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.common;

/*
 * comment.
 *  결제 도메인의 응답 코드 상수 (PAYMENT-로 시작)
 * */

public final class PaymentResponseCode {

    private PaymentResponseCode() {}

    // 성공
    public static final String PAYMENT_PREPARED = "PAYMENT-PREPARED";

    // 실패
    public static final String PLAN_NOT_PURCHASABLE = "PAYMENT-PLAN-NOT-PURCHASABLE";
    public static final String MONTHS_INVALID       = "PAYMENT-MONTHS-INVALID";
    public static final String GATEWAY_UNAVAILABLE  = "PAYMENT-GATEWAY-UNAVAILABLE";

}
