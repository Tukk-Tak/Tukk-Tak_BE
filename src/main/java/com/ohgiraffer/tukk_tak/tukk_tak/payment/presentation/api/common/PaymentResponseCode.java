package com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.common;

/*
 * comment.
 *  결제 도메인의 응답 코드 상수 (PAYMENT-로 시작)
 * */

public final class PaymentResponseCode {

    private PaymentResponseCode() {}

    // 성공
    public static final String PAYMENT_PREPARED  = "PAYMENT-PREPARED";
    public static final String PAYMENT_QUOTED    = "PAYMENT-QUOTED";
    public static final String PAYMENT_CONFIRMED = "PAYMENT-CONFIRMED";
    public static final String PAYMENT_HISTORY_FETCHED = "PAYMENT-HISTORY-FETCHED";

    // 실패
    public static final String PLAN_NOT_PURCHASABLE      = "PAYMENT-PLAN-NOT-PURCHASABLE";
    public static final String MONTHS_INVALID            = "PAYMENT-MONTHS-INVALID";
    public static final String GATEWAY_UNAVAILABLE       = "PAYMENT-GATEWAY-UNAVAILABLE";
    public static final String PAYMENT_PENDING_NOT_FOUND = "PAYMENT-PENDING-NOT-FOUND";
    public static final String PAYMENT_ORDER_MISMATCH    = "PAYMENT-ORDER-MISMATCH";
    public static final String PAYMENT_GATEWAY_REJECTED  = "PAYMENT-GATEWAY-REJECTED";
    public static final String PAYMENT_IN_PROGRESS       = "PAYMENT-IN-PROGRESS";
    public static final String PAYMENT_ALREADY_PROCESSED = "PAYMENT-ALREADY-PROCESSED";
    public static final String PAYMENT_ACTIVATION_FAILED = "PAYMENT-ACTIVATION-FAILED";

}
