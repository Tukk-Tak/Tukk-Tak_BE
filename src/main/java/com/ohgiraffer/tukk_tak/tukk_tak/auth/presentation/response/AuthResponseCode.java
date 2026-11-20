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
    public static final String EMAIL_SENT = "AUTH-EMAIL-SENT";
    public static final String EMAIL_VERIFIED = "AUTH-EMAIL-VERIFIED";
    public static final String NICKNAME_CHECKED = "AUTH-NICKNAME-CHECKED";

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
    public static final String EMAIL_ALREADY_REGISTERED  = "AUTH-EMAIL-ALREADY-REGISTERED";
    public static final String EMAIL_SEND_TOO_FAST       = "AUTH-EMAIL-SEND-TOO-FAST";
    public static final String EMAIL_SEND_LIMIT_EXCEEDED = "AUTH-EMAIL-SEND-LIMIT-EXCEEDED";
    public static final String EMAIL_SEND_FAILED         = "AUTH-EMAIL-SEND-FAILED";
    public static final String EMAIL_CODE_EXPIRED  = "AUTH-EMAIL-CODE-EXPIRED";
    public static final String EMAIL_CODE_MISMATCH = "AUTH-EMAIL-CODE-MISMATCH";
    public static final String EMAIL_VERIFY_LOCKED = "AUTH-EMAIL-VERIFY-LOCKED";
    public static final String NICKNAME_INVALID                = "AUTH-NICKNAME-INVALID";
    public static final String NICKNAME_CHECK_LIMIT_EXCEEDED   = "AUTH-NICKNAME-CHECK-LIMIT-EXCEEDED";

}
