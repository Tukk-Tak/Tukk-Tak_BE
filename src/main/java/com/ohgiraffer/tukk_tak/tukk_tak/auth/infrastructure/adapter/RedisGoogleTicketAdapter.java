package com.ohgiraffer.tukk_tak.tukk_tak.auth.infrastructure.adapter;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port.GoogleSignupTicketPort;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.model.GoogleIdentity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

/*
 * comment.
 *  GoogleSignupTicketPort의 Redis 구현체
 *  이메일과 이름을 JSON 없이 줄바꿈으로 구분한 문자열 하나로 저장한다.
 *  값 저장과 만료 설정을 한 번의 명령으로 처리해서, 만료가 없는 티켓이 남을 수 없다.
 * */

@Component
@RequiredArgsConstructor
public class RedisGoogleTicketAdapter implements GoogleSignupTicketPort {

    // 가입 티켓을 저장하는 Redis 키 (google:ticket:{ticket})
    private static final String TICKET_KEY = "google:ticket:";
    // 이메일과 이름의 구분자 (이메일에는 줄바꿈이 올 수 없어서 첫 줄은 항상 이메일)
    private static final String SEPARATOR = "\n";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void saveTicket(String ticket, GoogleIdentity identity, Duration ttl) {
        redisTemplate.opsForValue().set(TICKET_KEY + ticket,
                identity.email() + SEPARATOR + identity.name(), ttl);
    }

    @Override
    public Optional<GoogleIdentity> findTicket(String ticket) {
        String value = redisTemplate.opsForValue().get(TICKET_KEY + ticket);
        if (value == null) {
            return Optional.empty();
        }
        // 첫 줄이 이메일, 나머지가 이름 (이름이 비어 있어도 빈 문자열로 복원됨)
        String[] parts = value.split(SEPARATOR, 2);
        return Optional.of(new GoogleIdentity(parts[0], parts.length == 2 ? parts[1] : ""));
    }

    @Override
    public void deleteTicket(String ticket) {
        redisTemplate.delete(TICKET_KEY + ticket);
    }

}
