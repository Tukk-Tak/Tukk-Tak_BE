package com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model;

import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;

import java.time.LocalDate;
import java.time.LocalDateTime;

/*
 * comment.
 *  결제 내역 한 줄 (조회 전용 값)
 *  플랜과 이용 기간은 결제로 만든 구독 행에서 오고, 개월 수와 금액은 결제 행에서 온다.
 * */

public record PaymentHistory(
        String orderId,
        PaymentMethod method,
        PlanName planName,
        int months,
        int amount,
        LocalDate startDate,        // 이용 시작일
        LocalDate endDate,          // 이용 종료일 (상위 플랜 구매로 밀렸으면 밀린 날짜)
        LocalDateTime paidAt
) {
}
