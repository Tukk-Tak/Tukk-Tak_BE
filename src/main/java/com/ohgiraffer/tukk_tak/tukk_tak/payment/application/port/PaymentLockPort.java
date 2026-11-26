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

    // 내 토큰일 때만 락을 푼다 (ttl이 지나 다른 요청이 잡은 락을 지우지 않기 위해)
    void unlock(Long memberId, String token);

}
