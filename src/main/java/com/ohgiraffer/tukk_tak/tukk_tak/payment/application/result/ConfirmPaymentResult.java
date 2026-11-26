package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentMethod;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;

import java.time.LocalDate;
import java.time.LocalDateTime;

/*
 * comment.
 *  결제 승인 결과 (결제 완료 화면에 필요한 값)
 * */

public record ConfirmPaymentResult(
        String orderId,
        PaymentMethod method,
        PlanName planName,
        int months,
        int amount,
        LocalDate startDate,        // 이용 시작일
        LocalDate endDate,          // 이용 종료일 (당일까지 이용)
        LocalDateTime paidAt
) {
}
