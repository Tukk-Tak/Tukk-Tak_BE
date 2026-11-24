package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.response;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.GoogleLoginResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.LoginResult;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Role;

/*
 * comment.
 *  구글 로그인 응답 데이터
 *  registered가 true면 로그인 값(accessToken, role, nickname)이, false면 가입 값(signupTicket, email, name)이 채워진다.
 *  refresh token은 본문이 아니라 HttpOnly 쿠키로 전달한다.
 * */

public record GoogleLoginResponse(
        boolean registered,     // true: 로그인 완료, false: 추가 정보 입력 필요
        String accessToken,     // registered가 true일 때만 값이 있음
        Role role,              // registered가 true일 때만 값이 있음
        String nickname,        // registered가 true일 때만 값이 있음
        String signupTicket,    // registered가 false일 때만 값이 있음
        String email,           // registered가 false일 때만 값이 있음
        String name             // registered가 false일 때만 값이 있음
) {
    // application 결과를 응답 DTO로 변환 (refresh token은 일부러 제외)
    public static GoogleLoginResponse from(GoogleLoginResult result) {
        if (result.registered()) {
            LoginResult login = result.login();
            return new GoogleLoginResponse(true, login.accessToken(), login.role(), login.nickname(),
                    null, null, null);
        }
        return new GoogleLoginResponse(false, null, null, null,
                result.signupTicket(), result.email(), result.name());
    }
}
