package com.ohgiraffer.tukk_tak.tukk_tak.global.security.token;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/*
 * comment.
 *  로그아웃된 access token의 jti를 Redis 블랙리스트에 등록 (키: blacklist:{jti})
 *  토큰의 남은 만료시간만큼만 저장해서, 토큰이 만료되면 Redis에서도 자동으로 사라진다.
 * */

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private static final String KEY_PREFIX = "blacklist:";

    private final StringRedisTemplate redisTemplate;

    /** 로그아웃된 access token을 남은 만료시간만큼만 블랙리스트로 (TTL이 끝나면 Redis에서도 자동 삭제) */
    public void blacklist(String jti, long remainingValidityMs) {
        if (jti == null || remainingValidityMs <= 0) {
            return;
        }
        redisTemplate.opsForValue().set(KEY_PREFIX + jti, "logout", Duration.ofMillis(remainingValidityMs));
    }

    public boolean isBlacklisted(String jti) {
        if (jti == null) {
            return false;
        }
        return Boolean.TRUE.equals(redisTemplate.hasKey(KEY_PREFIX + jti));
    }

}
