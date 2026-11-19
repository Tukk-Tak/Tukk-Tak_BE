package com.ohgiraffer.tukk_tak.tukk_tak.global.security.user;

import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Member;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.MemberStatus;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

/*
 * comment.
 *  요청마다 DB를 조회하지 않도록 회원 정보(email, role, status)를 Redis에 5분 동안 캐시
 *  회원의 권한/상태가 바뀌면(탈퇴 등) 반드시 evict()로 지워야 한다.
 * */

@Component
@RequiredArgsConstructor
public class PrincipalCache {

    private static final String KEY_PREFIX = "member:principal:";
    private static final Duration TTL = Duration.ofMinutes(5);

    private final StringRedisTemplate redisTemplate;

    public Optional<CustomUserPrincipal> get(Long memberId) {
        String key = KEY_PREFIX + memberId;
        Map<Object, Object> values = redisTemplate.opsForHash().entries(key);
        if (values.isEmpty()) return Optional.empty();
        try {
            return Optional.of(CustomUserPrincipal.ofCached(
                    memberId,
                    (String) values.get("email"),
                    Role.valueOf((String) values.get("role")),
                    MemberStatus.valueOf((String) values.get("status"))));
        } catch (RuntimeException e) {
            redisTemplate.delete(key);   // 깨진 캐시는 버리고 DB에서 다시 읽음
            return Optional.empty();
        }
    }

    public void put(Member member) {
        String key = KEY_PREFIX + member.getId();
        redisTemplate.opsForHash().putAll(key, Map.of(
                "email", member.getEmail(),
                "role", member.getRole().name(),
                "status", member.getStatus().name()));
        redisTemplate.expire(key, TTL);
    }

    public void evict(Long memberId) { redisTemplate.delete(KEY_PREFIX + memberId); }

}
