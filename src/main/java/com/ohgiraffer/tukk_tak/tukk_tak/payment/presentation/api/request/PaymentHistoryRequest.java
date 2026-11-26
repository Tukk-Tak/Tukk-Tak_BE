package com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/*
 * comment.
 *  결제 내역 조회 요청 (쿼리스트링: ?page=0&size=10)
 *  안 보내면 첫 페이지, 10건으로 본다. 한 번에 가져오는 양을 50건으로 막아 부하를 제한한다.
 * */

public record PaymentHistoryRequest(
        @Min(value = 0, message = "페이지는 0 이상이어야 합니다.")
        Integer page,

        @Min(value = 1, message = "한 페이지 크기는 1 이상이어야 합니다.")
        @Max(value = 50, message = "한 페이지 크기는 50 이하여야 합니다.")
        Integer size
) {
    public PaymentHistoryRequest {
        // 값을 안 보내면 기본값으로 채운다
        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 10;
        }
    }
}
