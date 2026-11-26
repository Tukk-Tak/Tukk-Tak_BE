package com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.command.TossWebhookCommand;

/*
 * comment.
 *  토스 웹훅 요청 본문
 *  이벤트 종류와 결제키만 읽고, 나머지는 무시한다. (상태와 주문번호는 PG에 조회해서 정한다)
 *  이벤트 종류마다 data의 모양이 달라 필수 검증을 걸지 않고, 모르는 값은 서비스가 걸러서 200으로 돌려준다.
 * */

public record TossWebhookRequest(
        String eventType,
        Data data
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Data(String paymentKey) {
    }

    public TossWebhookCommand toCommand() {
        return new TossWebhookCommand(eventType, data == null ? null : data.paymentKey());
    }
}
