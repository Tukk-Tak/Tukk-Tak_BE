package com.ohgiraffer.tukk_tak.tukk_tak.payment.infrastructure.adapter;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.port.PaymentLockPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/*
 * comment.
 *  회원 단위 결제 락의 Redis 구현
 *  키: payment:lock:{memberId}, 값: 락을 잡은 요청의 토큰 (SET NX EX로 잡는다)
 *  해제는 "값이 내 토큰일 때만 삭제"를 Lua 스크립트 한 번으로 해서, 비교와 삭제 사이에 끼어들 틈이 없게 한다.
 * */

@Component
@RequiredArgsConstructor
public class RedisPaymentLockAdapter implements PaymentLockPort {

    private static final String LOCK_KEY = "payment:lock:";
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end",
            Long.class);

    private final StringRedisTemplate redisTemplate;

    @Override
    public Optional<String> tryLock(Long memberId, Duration ttl) {
        String token = UUID.randomUUID().toString();
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(LOCK_KEY + memberId, token, ttl);
        return Boolean.TRUE.equals(locked) ? Optional.of(token) : Optional.empty();
    }

    @Override
    public void unlock(Long memberId, String token) {
        redisTemplate.execute(UNLOCK_SCRIPT, List.of(LOCK_KEY + memberId), token);
    }

}
