package com.ohgiraffer.tukk_tak.tukk_tak.global.security.token;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/*
 * comment.
 *  비밀번호를 바꾼 회원의 기존 access token을 모두 무효로 만든다 (키: member:tokens-valid-after:{회원id})
 *  저장한 시각보다 먼저 발급된 토큰은 거절하고, 키는 access token 유효시간만큼만 둔다.
 *  그 시간이 지나면 옛 토큰은 어차피 만료되므로 DB 컬럼 없이 Redis만으로 충분하다.
 * */

@Service
@RequiredArgsConstructor
public class AccessTokenRevoker {

    private static final String KEY_PREFIX = "member:tokens-valid-after:";

    private final StringRedisTemplate redisTemplate;

    // access token 유효기간 (ms), JwtTokenProvider와 같은 설정값
    @Value("${jwt.access-token-validity}") private long accessTokenValidityMs;

    // 지금까지 발급된 이 회원의 access token을 전부 무효로 만든다
    // JWT의 발급 시각(iat)은 초 단위라서, 기준을 "다음 초"로 잡아 같은 초에 발급된 옛 토큰도 걸러낸다
    public void revokeAll(Long memberId) {
        long validAfterSeconds = Instant.now().getEpochSecond() + 1;
        redisTemplate.opsForValue().set(KEY_PREFIX + memberId, String.valueOf(validAfterSeconds),
                Duration.ofMillis(accessTokenValidityMs));
    }

    // 이 토큰이 무효화 기준보다 먼저 발급됐으면 true
    public boolean isRevoked(Long memberId, Date issuedAt) {
        String validAfter = redisTemplate.opsForValue().get(KEY_PREFIX + memberId);
        if (validAfter == null) {
            return false;
        }
        // 발급 시각을 알 수 없는 토큰은 안전하게 무효로 본다
        return issuedAt == null || issuedAt.getTime() / 1000 < Long.parseLong(validAfter);
    }

}
