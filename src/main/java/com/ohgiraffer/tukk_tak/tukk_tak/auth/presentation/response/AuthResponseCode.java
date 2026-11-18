package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.response;

/*
 * comment.
 *  인증 도메인의 응답 코드 상수 (AUTH-로 시작)
 * */

public final class AuthResponseCode {

    private AuthResponseCode() {}

    // 성공
    public static final String LOGIN_SUCCESS   = "AUTH-LOGIN-SUCCESS";
    public static final String LOGOUT_SUCCESS  = "AUTH-LOGOUT-SUCCESS";
    public static final String TOKEN_REFRESHED = "AUTH-TOKEN-REFRESHED";

    // 실패
    public static final String LOGIN_FAILED               = "AUTH-LOGIN-FAILED";
    public static final String SOCIAL_ACCOUNT_NO_PASSWORD = "AUTH-SOCIAL-ACCOUNT-NO-PASSWORD";
    public static final String WITHDRAWN_MEMBER           = "AUTH-WITHDRAWN-MEMBER";
    public static final String ACCOUNT_LOCKED             = "AUTH-ACCOUNT-LOCKED";
    public static final String MISSING_ACCESS_TOKEN       = "AUTH-MISSING-ACCESS-TOKEN";
    public static final String INVALID_TOKEN              = "AUTH-INVALID-TOKEN";
    public static final String MISSING_REFRESH_TOKEN      = "AUTH-MISSING-REFRESH-TOKEN";
    public static final String INVALID_REFRESH_TOKEN      = "AUTH-INVALID-REFRESH-TOKEN";
    public static final String ALREADY_LOGGED_OUT         = "AUTH-ALREADY-LOGGED-OUT";

}
