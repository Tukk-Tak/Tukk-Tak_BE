package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.response;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.TokenResult;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Role;

/*
 * comment.
 *  access token 재발급 응답 데이터
 * */

public record TokenResponse(
        Long memberId,
        String accessToken,
        Role role
) {
    public static TokenResponse from(TokenResult result) {
        return new TokenResponse(result.memberId(), result.accessToken(), result.role());
    }
}
