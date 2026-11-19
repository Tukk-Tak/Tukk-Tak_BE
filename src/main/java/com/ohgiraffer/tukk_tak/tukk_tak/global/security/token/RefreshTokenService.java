package com.ohgiraffer.tukk_tak.tukk_tak.global.security.token;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/*
 * comment.
 *  Redis에 refresh token을 저장 / 검증 / 삭제 (키: refresh:{회원id})
 *  회원당 1개만 저장하므로 다른 기기에서 로그인하면 이전 토큰은 무효가 된다.
 * */

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final String KEY_PREFIX = "refresh:";

    private final StringRedisTemplate redisTemplate;

    public void save(Long userId, String refreshToken, Duration ttl) {
        redisTemplate.opsForValue().set(KEY_PREFIX + userId, refreshToken, ttl);
    }

    public boolean isValid(Long userId, String refreshToken) {
        String stored = redisTemplate.opsForValue().get(KEY_PREFIX + userId);
        return refreshToken.equals(stored);
    }

    public void delete(Long userId) {
        redisTemplate.delete(KEY_PREFIX + userId);
    }

}
