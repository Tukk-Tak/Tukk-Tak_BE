package com.ohgiraffer.tukk_tak.tukk_tak.payment.infrastructure.adapter;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.port.PendingPaymentPort;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PendingPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Optional;

/*
 * comment.
 *  진행 중인 결제 주문 저장소의 Redis 구현
 *  키: payment:pending:{memberId}, 값: 주문 JSON (회원당 1개라서 새 주문이 오면 덮어쓴다)
 *  값과 TTL을 한 번에 저장(SET EX)해서, 저장 도중에 멈춰도 TTL 없는 키가 남지 않는다.
 * */

@Component
@RequiredArgsConstructor
public class RedisPendingPaymentAdapter implements PendingPaymentPort {

    private static final String PENDING_KEY = "payment:pending:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void save(Long memberId, PendingPayment pendingPayment, Duration ttl) {
        try {
            // 임시 주문을 JSON 문자열로 바꿔서 TTL과 함께 저장
            redisTemplate.opsForValue().set(PENDING_KEY + memberId,
                    objectMapper.writeValueAsString(pendingPayment), ttl);
        } catch (JacksonException e) {
            // 단순한 record라 사실상 일어나지 않는다. 일어나면 코드 문제이므로 그대로 올려 보낸다
            throw new IllegalStateException("결제 임시 주문 직렬화 실패", e);
        }
    }

    @Override
    public Optional<PendingPayment> find(Long memberId) {
        String json = redisTemplate.opsForValue().get(PENDING_KEY + memberId);
        if (json == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(json, PendingPayment.class));
        } catch (JacksonException e) {
            // 우리가 저장한 값이라 사실상 일어나지 않는다 (깨진 값은 주문이 없는 것으로 본다)
            return Optional.empty();
        }
    }

    @Override
    public void delete(Long memberId) {
        redisTemplate.delete(PENDING_KEY + memberId);
    }

}
