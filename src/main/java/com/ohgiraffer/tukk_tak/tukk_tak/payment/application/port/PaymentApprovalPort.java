package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.port;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.command.PgApproveCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.PgApproval;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentMethod;

import java.util.Optional;

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

    // 승인 요청이 실패로 끝났을 때, PG에서는 이미 승인됐는지 직접 조회한다 (승인됐으면 승인 정보, 아니면 empty)
    // 기본은 "조회 수단 없음"이다. 같은 요청을 다시 보내면 처음 결과를 돌려주는 PG(토스의 Idempotency-Key)는 덮어쓰지 않아도 된다
    default Optional<PgApproval> findApproved(PgApproveCommand command) {
        return Optional.empty();
    }

    // 승인한 결제를 전액 취소한다 (DB 반영 실패 같은 보상 처리용, 실패하면 예외)
    void cancel(String paymentKey, int amount, String reason);

}
