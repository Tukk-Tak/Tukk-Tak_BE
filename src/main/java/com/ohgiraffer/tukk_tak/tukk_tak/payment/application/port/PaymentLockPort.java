package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.port;

import java.time.Duration;
import java.util.Optional;

/*
 * comment.
 *  회원 단위 결제 락 포트
 *  더블클릭이나 동시 요청으로 같은 회원의 결제가 두 번 승인되는 것을 막는다.
 * */

public interface PaymentLockPort {

    // 락을 잡는다. 잡으면 해제할 때 쓸 토큰을, 이미 잡혀 있으면 빈 값을 돌려준다
    Optional<String> tryLock(Long memberId, Duration ttl);

    // 내 토큰일 때만 락의 유효 시간을 다시 채운다. ttl이 지나 락을 잃었으면 false (PG 호출 직전에 주인인지 확인하는 용도)
    boolean renew(Long memberId, String token, Duration ttl);

    // 내 토큰일 때만 락을 푼다 (ttl이 지나 다른 요청이 잡은 락을 지우지 않기 위해)
    void unlock(Long memberId, String token);

}
