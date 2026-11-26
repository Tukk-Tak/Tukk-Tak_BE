package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentHistory;

import java.util.List;

/*
 * comment.
 *  결제 내역 조회 결과 (한 페이지와 페이지 정보)
 * */

public record PaymentHistoryResult(
        List<PaymentHistory> content,
        int page,               // 현재 페이지 (0부터)
        int size,               // 한 페이지 크기
        long totalElements,     // 전체 결제 건수
        int totalPages          // 전체 페이지 수
) {
}
