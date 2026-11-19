package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result;

import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Role;

import java.time.Duration;

/*
 * comment.
 *  로그인 결과 (응답 본문 + refresh token + refresh token 유효기간)
 *  서비스가 컨트롤러에게 돌려주며, 컨트롤러는 refresh token을 쿠키에 담는다.
 * */

public record LoginResult(
        String accessToken,      // 응답 본문으로 내려줄 access token
        String refreshToken,     // 쿠키에 담을 refresh token
        Duration refreshTtl,     // refresh token 유효기간 (Redis 저장 기간, 유지 쿠키의 maxAge)
        boolean rememberMe,      // true면 유지 쿠키, false면 세션 쿠키로 내려준다
        Role role,
        String nickname
) {
}
