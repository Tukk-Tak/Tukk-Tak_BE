package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.response;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.LoginResult;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Role;

/*
 * comment.
 *  로그인 성공 응답 데이터 (access token, 권한, 닉네임)
 *  refresh token은 본문이 아니라 HttpOnly 쿠키로 전달한다.
 * */

public record LoginResponse(
        String accessToken,
        Role role,
        String nickname
) {
    // application 결과를 응답 DTO로 변환 (refresh token은 일부러 제외)
    public static LoginResponse from(LoginResult result) {
        return new LoginResponse(result.accessToken(), result.role(), result.nickname());
    }
}
