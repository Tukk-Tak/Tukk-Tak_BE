package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.port;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PendingPayment;

import java.time.Duration;

/*
 * comment.
 *  결제 준비 단계의 임시 주문 저장소 포트(인터페이스)
 *  회원당 진행 중인 주문은 1개라서, 저장하면 이전 주문을 덮어쓴다.
 * */

public interface PendingPaymentPort {

    // 회원의 임시 주문 저장 (TTL이 지나면 저절로 사라진다)
    void save(Long memberId, PendingPayment pendingPayment, Duration ttl);

}
