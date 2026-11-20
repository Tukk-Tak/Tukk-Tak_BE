package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.response;

/*
 * comment.
 *  인증 도메인의 응답 메시지 상수
 * */


public final class AuthResponseMessage {

    private AuthResponseMessage() {}

    public static final String LOGIN_SUCCESS   = "로그인되었습니다.";
    public static final String LOGOUT_SUCCESS  = "로그아웃되었습니다.";
    public static final String TOKEN_REFRESHED = "토큰이 재발급되었습니다.";
    public static final String EMAIL_SENT = "인증번호를 보냈어요.";
    public static final String EMAIL_VERIFIED = "이메일 인증이 완료되었어요.";
    public static final String NICKNAME_CHECKED = "닉네임 확인이 완료되었어요.";

    public static final String LOGIN_FAILED               = "이메일 또는 비밀번호가 올바르지 않습니다.";
    public static final String SOCIAL_ACCOUNT_NO_PASSWORD = "구글 로그인으로 가입된 계정입니다. 구글 로그인을 이용해주세요.";
    public static final String WITHDRAWN_MEMBER           = "탈퇴한 계정입니다.";
    public static final String ACCOUNT_LOCKED             = "로그인 실패 횟수 초과로 일시적으로 잠긴 계정입니다. 15분 후 다시 시도해주세요.";
    public static final String MISSING_ACCESS_TOKEN       = "AccessToken이 필요합니다.";
    public static final String INVALID_TOKEN              = "유효하지 않은 토큰입니다.";
    public static final String MISSING_REFRESH_TOKEN      = "RefreshToken이 필요합니다.";
    public static final String INVALID_REFRESH_TOKEN      = "유효하지 않은 refresh token입니다. 다시 로그인해주세요.";
    public static final String ALREADY_LOGGED_OUT         = "이미 로그아웃 되었습니다.";
    public static final String EMAIL_ALREADY_REGISTERED  = "이미 가입된 이메일이에요.";
    public static final String EMAIL_SEND_TOO_FAST       = "인증번호는 1분에 한 번만 받을 수 있어요. 잠시 후 다시 시도해주세요.";
    public static final String EMAIL_SEND_LIMIT_EXCEEDED = "요청이 너무 많아요. 잠시 후 다시 시도해주세요.";
    public static final String EMAIL_SEND_FAILED         = "인증 메일을 보내지 못했어요. 잠시 후 다시 시도해주세요.";
    public static final String EMAIL_CODE_EXPIRED  = "인증번호가 만료되었어요. 인증번호를 다시 받아주세요.";
    public static final String EMAIL_CODE_MISMATCH = "인증번호가 올바르지 않아요.";
    public static final String EMAIL_VERIFY_LOCKED = "인증번호를 여러 번 잘못 입력했어요. 5분 후 다시 시도해주세요.";
    public static final String NICKNAME_INVALID              = "닉네임은 한글, 영문, 숫자로 2~15자까지 입력할 수 있어요.";
    public static final String NICKNAME_CHECK_LIMIT_EXCEEDED = "요청이 너무 많아요. 잠시 후 다시 시도해주세요.";

}
