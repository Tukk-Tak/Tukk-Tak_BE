package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.model.GoogleIdentity;

/*
 * comment.
 *  구글 로그인 결과
 *  이미 가입한 구글 회원이면 로그인 결과를, 처음 온 계정이면 가입 티켓과 구글 정보를 담는다.
 * */

public record GoogleLoginResult(
        boolean registered,       // 이미 가입한 구글 회원인지
        LoginResult login,        // registered가 true일 때만 값이 있음
        String signupTicket,      // registered가 false일 때만 값이 있음
        String email,             // registered가 false일 때만 값이 있음
        String name               // registered가 false일 때만 값이 있음
) {
    // 이미 가입한 회원의 로그인 결과
    public static GoogleLoginResult loggedIn(LoginResult login) {
        return new GoogleLoginResult(true, login, null, null, null);
    }

    // 처음 온 구글 계정 (가입 화면에서 추가 정보를 받아야 함)
    public static GoogleLoginResult signupRequired(String signupTicket, GoogleIdentity identity) {
        return new GoogleLoginResult(false, null, signupTicket, identity.email(), identity.name());
    }
}
