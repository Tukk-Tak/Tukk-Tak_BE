package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result;

import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;

import java.time.LocalDate;

/*
 * comment.
 *  결제 미리보기 결과 (금액과 이용 기간)
 *  프론트가 날짜 규칙을 따로 구현하지 않도록 서버가 계산한 값을 그대로 보여준다.
 * */

public record QuotePaymentResult(
        PlanName planName,
        int months,
        int monthlyPrice,       // 1개월 가격
        int amount,             // 결제 금액 (1개월 가격 × 개월 수)
        LocalDate startDate,    // 이용 시작일
        LocalDate endDate       // 이용 종료일 (당일까지 이용)
) {
}
