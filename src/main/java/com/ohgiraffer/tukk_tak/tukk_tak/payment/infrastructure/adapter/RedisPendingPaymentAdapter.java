package com.ohgiraffer.tukk_tak.tukk_tak.payment.infrastructure.adapter;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.port.PendingPaymentPort;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PendingPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

/*
 * comment.
 *  결제 임시 주문 저장소의 Redis 구현
 *  키: payment:pending:{memberId}, 값: 임시 주문 JSON
 *  키에 접두어를 붙여서 다른 키와 섞이지 않게 하고, 모든 키에 TTL을 걸어서 지우지 않아도 사라지게 한다.
 *  회원 번호를 키로 써서 회원당 1개만 남고, 다시 저장하면 이전 주문을 덮어쓴다.
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

}
