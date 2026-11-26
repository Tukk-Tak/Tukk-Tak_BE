package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.port;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.command.PgApproveCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.PgApproval;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentMethod;

/*
 * comment.
 *  PG 승인/취소 포트 (결제수단마다 구현체가 하나씩 있다)
 *  서비스는 결제수단으로 알맞은 구현체를 골라 쓰고, PG의 주소와 인증 방식은 모른다.
 * */

public interface PaymentApprovalPort {

    // 이 구현체가 맡는 결제수단
    PaymentMethod method();

    // PG에 승인을 요청한다 (거절이면 PaymentException.gatewayRejected, 통신 실패면 gatewayUnavailable)
    PgApproval approve(PgApproveCommand command);

    // 승인한 결제를 전액 취소한다 (DB 반영 실패 같은 보상 처리용, 실패하면 예외)
    void cancel(String paymentKey, int amount, String reason);

}
