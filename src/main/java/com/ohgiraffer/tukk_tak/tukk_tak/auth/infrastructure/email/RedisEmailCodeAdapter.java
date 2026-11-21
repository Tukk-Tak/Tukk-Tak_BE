package com.ohgiraffer.tukk_tak.tukk_tak.auth.infrastructure.email;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port.EmailCodePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/*
 * comment.
 *  인증번호 저장소의 Redis 구현
 *  키에 접두어(email:code:, email:cooldown:)를 붙여서 다른 키와 섞이지 않게 한다.
 *  모든 키에 TTL이 있어서 지우지 않아도 알아서 사라진다.
 * */

@Component
@RequiredArgsConstructor
public class RedisEmailCodeAdapter implements EmailCodePort {

    private static final String CODE_KEY = "email:code:";
    private static final String COOLDOWN_KEY = "email:cooldown:";
    private static final String TOKEN_KEY = "email:token:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean acquireCooldown(String email, Duration cooldown) {
        // setIfAbsent = SET NX EX : 키가 없을 때만 저장한다 (이미 있으면 false)
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(COOLDOWN_KEY + email, "1", cooldown);
        return Boolean.TRUE.equals(acquired);
    }

    @Override
    public void saveCode(String email, String code, Duration ttl) {
        redisTemplate.opsForValue().set(CODE_KEY + email, code, ttl);
    }

    @Override
    public void clear(String email) {
        redisTemplate.delete(List.of(CODE_KEY + email, COOLDOWN_KEY + email));
    }

    @Override
    public String findCode(String email) {
        return redisTemplate.opsForValue().get(CODE_KEY + email);   // 없으면 null
    }

    @Override
    public void deleteCode(String email) {
        redisTemplate.delete(CODE_KEY + email);
    }

    @Override
    public void saveToken(String token, String email, Duration ttl) {
        redisTemplate.opsForValue().set(TOKEN_KEY + token, email, ttl);
    }

    @Override
    public String findEmailByToken(String token) {
        return redisTemplate.opsForValue().get(TOKEN_KEY + token);
    }

    @Override
    public void deleteToken(String token) {
        redisTemplate.delete(TOKEN_KEY + token);
    }

}
