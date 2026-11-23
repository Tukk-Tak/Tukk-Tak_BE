package com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.common;

/*
 * comment.
 *  결제 도메인의 응답 메시지 상수
 * */

public final class PaymentResponseMessage {

    private PaymentResponseMessage() {}

    public static final String PAYMENT_PREPARED = "결제 준비가 완료되었어요.";

    public static final String PLAN_NOT_PURCHASABLE = "구매할 수 없는 플랜입니다.";
    public static final String MONTHS_INVALID       = "구매할 수 없는 개월 수입니다.";
    public static final String GATEWAY_UNAVAILABLE  = "결제 서비스가 일시적으로 불안정합니다. 잠시 후 다시 시도해주세요.";

}
