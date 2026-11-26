package com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model;

import java.util.List;

/*
 * comment.
 *  결제 내역 한 페이지 (도메인이 Spring Data의 Page를 알지 않도록 직접 만든 값)
 * */

public record PaymentHistoryPage(
        List<PaymentHistory> content,
        long totalElements          // 전체 결제 건수
) {
}
