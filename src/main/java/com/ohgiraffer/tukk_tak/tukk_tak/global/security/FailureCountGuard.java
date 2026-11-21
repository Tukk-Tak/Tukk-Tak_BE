package com.ohgiraffer.tukk_tak.tukk_tak.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/*
 * comment.
 *  Redis 기반 실패 횟수 카운터 및 잠금 도구 (로그인, 이메일 인증 등 공용)
 *  지정한 기간 안에 최대 횟수만큼 실패하면 일정 시간 동안 잠근다.
 *  Lua 스크립트로 카운트 증가와 잠금을 한 번에 처리해서 동시 요청에도 정확하다.
 * */

@Component
@RequiredArgsConstructor
public class FailureCountGuard {

    // 실패 횟수를 세다가 너무 많이 틀리면 잠근다
    // 1. 실패 카운터 +1
    // 2. 이번이 첫 실패면 카운터에 유효기간을 걸어둠 (윈도우가 지나면 자동 리셋)
    // 3. 실패 횟수가 maxCount 이상이면 lockDuration 동안 차단
    private static final String RECORD_FAILURE_SCRIPT =
            "local count = redis.call('INCR', KEYS[1]) " +
                    "if count == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]) end " +
                    "if count >= tonumber(ARGV[2]) then redis.call('SET', KEYS[2], 'LOCKED', 'EX', ARGV[3]) end " +
                    "return count";

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> recordFailureScript =
            new DefaultRedisScript<>(RECORD_FAILURE_SCRIPT, Long.class);

    public boolean isLocked(String scope, String identifier) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey(scope, identifier)));
    }

    public void recordFailure(String scope, String identifier, int maxCount,
                              Duration failureWindow, Duration lockDuration) {
        redisTemplate.execute(
                recordFailureScript,
                List.of(failureKey(scope, identifier), lockKey(scope, identifier)),
                String.valueOf(failureWindow.getSeconds()),
                String.valueOf(maxCount),
                String.valueOf(lockDuration.getSeconds())
        );
    }

    public void resetFailure(String scope, String identifier) {
        redisTemplate.delete(failureKey(scope, identifier));
    }

    // 실패 횟수와 잠금을 모두 지운다 (이메일로 본인 확인을 마친 비밀번호 재설정처럼, 잠금을 풀어줘도 되는 경우에 쓴다)
    public void clear(String scope, String identifier) {
        redisTemplate.delete(List.of(failureKey(scope, identifier), lockKey(scope, identifier)));
    }

    private String failureKey(String scope, String identifier) {
        return "failure:" + scope + ":" + identifier;
    }

    private String lockKey(String scope, String identifier) {
        return "lock:" + scope + ":" + identifier;
    }

}
