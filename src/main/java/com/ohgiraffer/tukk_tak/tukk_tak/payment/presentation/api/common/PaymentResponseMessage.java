package com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.common;

/*
 * comment.
 *  결제 도메인의 응답 메시지 상수
 * */

public final class PaymentResponseMessage {

    private PaymentResponseMessage() {}

    public static final String PAYMENT_PREPARED  = "결제 준비가 완료되었어요.";
    public static final String PAYMENT_QUOTED    = "결제 예상 금액과 이용 기간을 조회했습니다.";
    public static final String PAYMENT_CONFIRMED = "결제가 완료되었습니다.";
    public static final String PAYMENT_HISTORY_FETCHED = "결제 내역을 조회했습니다.";

    public static final String PLAN_NOT_PURCHASABLE      = "구매할 수 없는 플랜입니다.";
    public static final String MONTHS_INVALID            = "구매할 수 없는 개월 수입니다.";
    public static final String GATEWAY_UNAVAILABLE       = "결제 서비스가 일시적으로 불안정합니다. 잠시 후 다시 시도해주세요.";
    public static final String PAYMENT_PENDING_NOT_FOUND = "진행 중인 결제를 찾을 수 없습니다. 결제를 처음부터 다시 시도해 주세요.";
    public static final String PAYMENT_ORDER_MISMATCH    = "결제 정보가 일치하지 않습니다. 결제를 처음부터 다시 시도해 주세요.";
    public static final String PAYMENT_GATEWAY_REJECTED  = "결제가 승인되지 않았습니다. 카드 한도나 잔액을 확인하거나 다른 결제수단으로 시도해 주세요.";
    public static final String PAYMENT_IN_PROGRESS       = "이미 결제를 처리하고 있습니다. 잠시 후 다시 확인해 주세요.";
    public static final String PAYMENT_ALREADY_PROCESSED = "이미 처리된 결제입니다.";
    public static final String PAYMENT_ACTIVATION_FAILED = "결제 처리 중 문제가 발생했습니다. 결제 후에도 이 화면이 보이면 문의해 주세요.";

}
