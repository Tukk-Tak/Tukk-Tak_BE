package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.port;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PendingPayment;

import java.time.Duration;
import java.util.Optional;

/*
 * comment.
 *  진행 중인 결제 주문 저장소 포트
 *  회원당 주문 1개만 두고, 결제 준비가 끝날 때마다 덮어쓴다.
 * */

public interface PendingPaymentPort {

    // 회원의 진행 중 주문을 저장한다 (이미 있으면 덮어쓴다, ttl이 지나면 사라진다)
    void save(Long memberId, PendingPayment pending, Duration ttl);

    // 회원의 진행 중 주문을 꺼낸다 (없거나 만료됐으면 빈 값)
    Optional<PendingPayment> find(Long memberId);

    // 회원의 진행 중 주문을 지운다
    void delete(Long memberId);

}
