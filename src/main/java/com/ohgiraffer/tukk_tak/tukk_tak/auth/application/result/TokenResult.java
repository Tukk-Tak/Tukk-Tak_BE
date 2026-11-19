package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result;

import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Role;

/*
 * comment.
 *  access token 재발급 결과
 * */

public record TokenResult(
        Long memberId,
        String accessToken,
        Role role
) {
}
