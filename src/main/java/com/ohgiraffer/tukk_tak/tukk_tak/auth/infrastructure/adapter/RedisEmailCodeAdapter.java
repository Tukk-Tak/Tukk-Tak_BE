package com.ohgiraffer.tukk_tak.tukk_tak.auth.infrastructure.adapter;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port.EmailCodePort;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.model.EmailPurpose;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

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
    public boolean acquireCooldown(EmailPurpose purpose, String email, Duration cooldown) {
        // setIfAbsent = SET NX EX : 키가 없을 때만 저장한다 (이미 있으면 false)
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(key(COOLDOWN_KEY, purpose, email), "1", cooldown);
        return Boolean.TRUE.equals(acquired);
    }

    @Override
    public void saveCode(EmailPurpose purpose, String email, String code, Duration ttl) {
        redisTemplate.opsForValue().set(key(CODE_KEY, purpose, email), code, ttl);
    }

    @Override
    public void clear(EmailPurpose purpose, String email) {
        redisTemplate.delete(List.of(key(CODE_KEY, purpose, email), key(COOLDOWN_KEY, purpose, email)));
    }

    @Override
    public String findCode(EmailPurpose purpose, String email) {
        return redisTemplate.opsForValue().get(key(CODE_KEY, purpose, email));   // 없으면 null
    }

    @Override
    public void deleteCode(EmailPurpose purpose, String email) {
        redisTemplate.delete(key(CODE_KEY, purpose, email));
    }

    @Override
    public void saveToken(EmailPurpose purpose, String token, String email, Duration ttl) {
        redisTemplate.opsForValue().set(key(TOKEN_KEY, purpose, token), email, ttl);
    }

    @Override
    public String findEmailByToken(EmailPurpose purpose, String token) {
        return redisTemplate.opsForValue().get(key(TOKEN_KEY, purpose, token));
    }

    @Override
    public void deleteToken(EmailPurpose purpose, String token) {
        redisTemplate.delete(key(TOKEN_KEY, purpose, token));
    }

    // 접두어 + 용도 + 값: email:code:password_reset:abc@x.com
    private String key(String prefix, EmailPurpose purpose, String value) {
        return prefix + purpose.name().toLowerCase(Locale.ROOT) + ":" + value;
    }

}
