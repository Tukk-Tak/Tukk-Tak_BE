package com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.response;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.ConfirmPaymentResult;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentMethod;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;

import java.time.LocalDate;
import java.time.LocalDateTime;

/*
 * comment.
 *  결제 승인 응답 (결제 완료 화면에 보여줄 값)
 * */

public record ConfirmPaymentResponse(
        String orderId,
        PaymentMethod method,
        PlanName planName,
        int months,
        int amount,
        LocalDate startDate,        // 이용 시작일
        LocalDate endDate,          // 이용 종료일 (당일까지 이용)
        LocalDateTime paidAt
) {
    public static ConfirmPaymentResponse from(ConfirmPaymentResult result) {
        return new ConfirmPaymentResponse(result.orderId(), result.method(), result.planName(),
                result.months(), result.amount(), result.startDate(), result.endDate(), result.paidAt());
    }
}
