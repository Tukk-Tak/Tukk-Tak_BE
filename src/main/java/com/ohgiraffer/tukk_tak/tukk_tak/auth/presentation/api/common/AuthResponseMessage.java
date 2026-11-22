package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.common;

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
    public static final String GOOGLE_LOGIN_SUCCESS    = "구글 로그인에 성공했습니다.";
    public static final String GOOGLE_SIGNUP_SUCCESS   = "가입이 완료되었습니다.";
    public static final String PASSWORD_RESET = "비밀번호가 변경되었어요. 새 비밀번호로 로그인해주세요.";
    public static final String PASSWORD_CHANGED = "비밀번호가 변경되었어요. 다시 로그인해주세요.";

    public static final String LOGIN_FAILED               = "이메일 또는 비밀번호가 올바르지 않습니다.";
    public static final String SOCIAL_ACCOUNT_NO_PASSWORD = "구글 로그인으로 가입된 계정입니다. 구글 로그인을 이용해주세요.";
    public static final String WITHDRAWN_MEMBER           = "탈퇴한 회원입니다. 탈퇴 후 1년 이내에는 같은 이메일로 다시 가입하면 이전 정보가 복구됩니다.";
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
    public static final String BUSINESS_VERIFIED              = "사업자 인증이 완료되었습니다.";
    public static final String BUSINESS_MISMATCH              = "국세청 정보와 일치하지 않아요. 개업 직후에는 반영까지 며칠 걸릴 수 있어요.";
    public static final String BUSINESS_CLOSED                = "폐업한 사업자는 인증할 수 없습니다.";
    public static final String BUSINESS_ALREADY_USED          = "이미 인증된 사업자등록번호입니다.";
    public static final String BUSINESS_VERIFY_LIMIT_EXCEEDED = "요청이 너무 많습니다. 잠시 후 다시 시도해주세요.";
    public static final String BUSINESS_VERIFY_UNAVAILABLE    = "사업자 인증 서비스가 일시적으로 불안정합니다. 잠시 후 다시 시도해주세요.";
    public static final String EMAIL_TOKEN_INVALID    = "이메일 인증이 만료되었습니다. 다시 인증해주세요.";
    public static final String BUSINESS_TOKEN_INVALID = "사업자 인증이 만료되었습니다. 다시 인증해주세요.";
    public static final String BUSINESS_NAME_MISMATCH = "이름이 사업자 대표자명과 다릅니다. 사업자 인증을 다시 진행해주세요.";
    public static final String NICKNAME_ALREADY_USED  = "이미 사용 중인 닉네임입니다.";
    public static final String GOOGLE_TOKEN_INVALID     = "구글 로그인에 실패했습니다. 다시 시도해주세요.";
    public static final String GOOGLE_EMAIL_UNVERIFIED  = "이메일이 인증되지 않은 구글 계정입니다.";
    public static final String LOCAL_ACCOUNT_EXISTS     = "이메일로 가입된 계정이에요. 이메일로 로그인해주세요.";
    public static final String GOOGLE_TOO_MANY_ATTEMPTS = "시도 횟수가 너무 많습니다. 잠시 후 다시 시도해주세요.";
    public static final String GOOGLE_TICKET_INVALID = "구글 인증이 만료되었습니다. 다시 로그인해주세요.";
    public static final String GOOGLE_SIGNUP_REQUIRED  = "추가 정보 입력이 필요합니다.";
    public static final String EMAIL_NOT_REGISTERED          = "가입되지 않은 이메일이에요.";
    public static final String PASSWORD_RESET_SOCIAL_ACCOUNT = "구글로 가입된 계정이에요. 구글 로그인을 이용해주세요.";
    public static final String PASSWORD_RESET_WITHDRAWN      = "탈퇴한 회원이에요. 탈퇴 후 1년 이내에는 같은 이메일로 다시 가입하면 이전 정보가 복구돼요.";
    public static final String PASSWORD_RESET_TOKEN_INVALID  = "비밀번호 재설정 인증이 만료되었어요. 인증번호를 다시 받아주세요.";
    public static final String PASSWORD_SAME_AS_CURRENT      = "현재 비밀번호와 다른 비밀번호를 입력해주세요.";
    public static final String CURRENT_PASSWORD_MISMATCH      = "현재 비밀번호가 올바르지 않아요.";
    public static final String PASSWORD_CHANGE_LOCKED         = "현재 비밀번호를 여러 번 틀려서 잠시 변경할 수 없어요. 15분 후 다시 시도해주세요.";
    public static final String PASSWORD_CHANGE_SOCIAL_ACCOUNT = "구글로 가입된 계정은 비밀번호를 변경할 수 없어요.";

}
