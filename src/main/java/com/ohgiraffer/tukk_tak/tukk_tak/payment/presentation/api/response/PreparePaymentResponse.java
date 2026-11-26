package com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.response;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.PreparePaymentResult;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentMethod;

import java.time.LocalDate;

/*
 * comment.
 *  결제 준비 응답 DTO
 *  startDate, endDate는 예상 기간이다. 실제 기간은 승인 때 다시 계산한다.
 *  redirect 주소는 카카오페이만 내려가고, 토스는 값이 없어서 응답에서 빠진다(NON_NULL).
 * */

public record PreparePaymentResponse(
        PaymentMethod method,
        String orderId,
        String orderName,
        int amount,
        LocalDate startDate,
        LocalDate endDate,
        String redirectPcUrl,
        String redirectMobileUrl
) {
    // 서비스 결과 -> 응답 DTO
    public static PreparePaymentResponse from(PreparePaymentResult result) {
        return new PreparePaymentResponse(result.method(), result.orderId(), result.orderName(),
                result.amount(), result.startDate(), result.endDate(),
                result.redirectPcUrl(), result.redirectMobileUrl());
    }
}
