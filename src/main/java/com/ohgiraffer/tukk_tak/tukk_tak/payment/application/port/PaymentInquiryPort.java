package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.port;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.PgPaymentInfo;

import java.util.Optional;

/*
 * comment.
 *  PG 결제 조회 포트
 *  웹훅 본문은 누구나 흉내 낼 수 있어서, 결제키로 PG에 직접 상태를 확인할 때 쓴다.
 * */

public interface PaymentInquiryPort {

    // 결제키로 PG에 결제 상태를 조회한다 (PG에 없는 결제키면 empty, 통신 실패면 PaymentException.gatewayUnavailable)
    Optional<PgPaymentInfo> inquire(String paymentKey);

}
