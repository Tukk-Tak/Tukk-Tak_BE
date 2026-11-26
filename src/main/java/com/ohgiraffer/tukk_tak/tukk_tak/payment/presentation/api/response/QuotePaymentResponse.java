package com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.response;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.QuotePaymentResult;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;

import java.time.LocalDate;

/*
 * comment.
 *  결제 미리보기 응답 (결제 금액과 이용 기간)
 * */

public record QuotePaymentResponse(
        PlanName planName,
        int months,
        int monthlyPrice,       // 1개월 가격
        int amount,             // 결제 금액
        LocalDate startDate,    // 이용 시작일
        LocalDate endDate       // 이용 종료일 (당일까지 이용)
) {
    public static QuotePaymentResponse from(QuotePaymentResult result) {
        return new QuotePaymentResponse(result.planName(), result.months(), result.monthlyPrice(),
                result.amount(), result.startDate(), result.endDate());
    }
}
