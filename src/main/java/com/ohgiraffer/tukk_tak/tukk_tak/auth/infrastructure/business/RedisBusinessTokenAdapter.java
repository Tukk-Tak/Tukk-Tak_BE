package com.ohgiraffer.tukk_tak.tukk_tak.auth.infrastructure.business;


import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port.BusinessTokenPort;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.model.BusinessTokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Optional;

/*
 * comment.
 *  사업자 확인 토큰 저장소의 Redis 구현
 *  키: business:token:{token}, 값: 사업자 정보 JSON
 *  키에 접두어를 붙여서 다른 키와 섞이지 않게 하고, 모든 키에 TTL을 걸어서 지우지 않아도 사라지게 한다.
 *  값과 TTL을 한 번에 저장(SET EX)해서, 저장 도중에 멈춰도 TTL 없는 키가 남지 않는다.
 * */

@Component
@RequiredArgsConstructor
public class RedisBusinessTokenAdapter implements BusinessTokenPort {

    private static final String TOKEN_KEY = "business:token:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void saveToken(String token, BusinessTokenInfo info, Duration ttl) {
        try {
            // 사업자 정보를 JSON 문자열로 바꿔서 TTL과 함께 저장
            redisTemplate.opsForValue().set(TOKEN_KEY + token, objectMapper.writeValueAsString(info), ttl);
        } catch (JacksonException e) {
            // 단순한 record라 사실상 일어나지 않는다. 일어나면 코드 문제이므로 그대로 올려 보낸다
            throw new IllegalStateException("사업자 토큰 직렬화 실패", e);
        }
    }

    @Override
    public Optional<BusinessTokenInfo> findToken(String token) {
        String json = redisTemplate.opsForValue().get(TOKEN_KEY + token);
        if (json == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(json, BusinessTokenInfo.class));
        } catch (JacksonException e) {
            // 우리가 저장한 값이라 사실상 일어나지 않는다 (깨진 값은 무효 토큰으로 본다)
            return Optional.empty();
        }
    }

    @Override
    public void deleteToken(String token) {
        redisTemplate.delete(TOKEN_KEY + token);
    }

}
