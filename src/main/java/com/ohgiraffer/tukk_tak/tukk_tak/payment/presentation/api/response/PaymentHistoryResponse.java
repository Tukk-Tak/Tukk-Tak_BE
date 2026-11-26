package com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.response;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.PaymentHistoryResult;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentHistory;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentMethod;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PaymentHistoryResponse(
        List<PaymentHistoryItem> content,
        int page,               // 현재 페이지 (0부터)
        int size,               // 한 페이지 크기
        long totalElements,     // 전체 결제 건수
        int totalPages          // 전체 페이지 수
) {
    public static PaymentHistoryResponse from(PaymentHistoryResult result) {
        return new PaymentHistoryResponse(
                result.content().stream().map(PaymentHistoryItem::from).toList(),
                result.page(), result.size(), result.totalElements(), result.totalPages());
    }

    // 결제 내역 한 줄
    public record PaymentHistoryItem(
            String orderId,
            PaymentMethod method,
            PlanName planName,
            int months,
            int amount,
            LocalDate startDate,        // 이용 시작일
            LocalDate endDate,          // 이용 종료일
            LocalDateTime paidAt
    ) {

        public static PaymentHistoryItem from(PaymentHistory history) {
            return new PaymentHistoryItem(history.orderId(), history.method(), history.planName(),
                    history.months(), history.amount(), history.startDate(), history.endDate(),
                    history.paidAt());
        }

    }
}
