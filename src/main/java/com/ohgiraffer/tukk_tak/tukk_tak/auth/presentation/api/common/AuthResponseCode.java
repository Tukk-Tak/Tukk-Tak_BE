package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.common;

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
    public static final String PASSWORD_RESET = "AUTH-PASSWORD-RESET";
    public static final String PASSWORD_CHANGED = "AUTH-PASSWORD-CHANGED";

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
    public static final String BUSINESS_VERIFIED              = "AUTH-BUSINESS-VERIFIED";
    public static final String BUSINESS_MISMATCH              = "AUTH-BUSINESS-MISMATCH";
    public static final String BUSINESS_CLOSED                = "AUTH-BUSINESS-CLOSED";
    public static final String BUSINESS_ALREADY_USED          = "AUTH-BUSINESS-ALREADY-USED";
    public static final String BUSINESS_VERIFY_LIMIT_EXCEEDED = "AUTH-BUSINESS-VERIFY-LIMIT-EXCEEDED";
    public static final String BUSINESS_VERIFY_UNAVAILABLE    = "AUTH-BUSINESS-VERIFY-UNAVAILABLE";
    public static final String EMAIL_TOKEN_INVALID    = "AUTH-EMAIL-TOKEN-INVALID";
    public static final String BUSINESS_TOKEN_INVALID = "AUTH-BUSINESS-TOKEN-INVALID";
    public static final String BUSINESS_NAME_MISMATCH = "AUTH-BUSINESS-NAME-MISMATCH";
    public static final String NICKNAME_ALREADY_USED  = "AUTH-NICKNAME-ALREADY-USED";
    public static final String GOOGLE_TOKEN_INVALID     = "AUTH-GOOGLE-TOKEN-INVALID";
    public static final String GOOGLE_EMAIL_UNVERIFIED  = "AUTH-GOOGLE-EMAIL-UNVERIFIED";
    public static final String LOCAL_ACCOUNT_EXISTS     = "AUTH-LOCAL-ACCOUNT-EXISTS";
    public static final String GOOGLE_TOO_MANY_ATTEMPTS = "AUTH-GOOGLE-TOO-MANY-ATTEMPTS";
    public static final String GOOGLE_TICKET_INVALID = "AUTH-GOOGLE-TICKET-INVALID";
    public static final String GOOGLE_LOGIN_SUCCESS    = "AUTH-GOOGLE-LOGIN-SUCCESS";
    public static final String GOOGLE_SIGNUP_REQUIRED  = "AUTH-GOOGLE-SIGNUP-REQUIRED";
    public static final String GOOGLE_SIGNUP_SUCCESS   = "AUTH-GOOGLE-SIGNUP-SUCCESS";
    public static final String EMAIL_NOT_REGISTERED          = "AUTH-EMAIL-NOT-REGISTERED";
    public static final String PASSWORD_RESET_SOCIAL_ACCOUNT = "AUTH-PASSWORD-RESET-SOCIAL-ACCOUNT";
    public static final String PASSWORD_RESET_WITHDRAWN      = "AUTH-PASSWORD-RESET-WITHDRAWN";
    public static final String PASSWORD_RESET_TOKEN_INVALID  = "AUTH-PASSWORD-RESET-TOKEN-INVALID";
    public static final String PASSWORD_SAME_AS_CURRENT      = "AUTH-PASSWORD-SAME-AS-CURRENT";
    public static final String CURRENT_PASSWORD_MISMATCH      = "AUTH-CURRENT-PASSWORD-MISMATCH";
    public static final String PASSWORD_CHANGE_LOCKED         = "AUTH-PASSWORD-CHANGE-LOCKED";
    public static final String PASSWORD_CHANGE_SOCIAL_ACCOUNT = "AUTH-PASSWORD-CHANGE-SOCIAL-ACCOUNT";

}
