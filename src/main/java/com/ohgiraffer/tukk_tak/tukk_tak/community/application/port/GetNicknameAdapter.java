package com.ohgiraffer.tukk_tak.tukk_tak.community.application.port;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GetNicknameAdapter implements GetNicknamePort {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public String getNickname(Long memberId) {
        return getNicknames(Set.of(memberId)).get(memberId);
    }

    @Override
    public Map<Long, String> getNicknames(Set<Long> memberIds) {
        if (memberIds.isEmpty()) {
            return Map.of();
        }

        Map<Long, String> result = new HashMap<>();
        jdbcTemplate.query(
                "SELECT id, nickname FROM member WHERE id IN (:ids)",
                Map.of("ids", memberIds),
                rs -> {
                    result.put(rs.getLong("id"), rs.getString("nickname"));
                });
        return result;
    }
}