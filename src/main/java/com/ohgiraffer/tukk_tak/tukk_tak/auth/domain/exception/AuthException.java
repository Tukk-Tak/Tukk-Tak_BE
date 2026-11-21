package com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.exception;

/*
 * comment.
 *  Auth 컨텍스트 전용 예외
 *  - 실패 종류(AuthFailure)만 들고 있고, 상태코드/문구는 AuthExceptionHandler가 정한다.
 *  - 로그인 실패는 반복 요청으로 자주 발생하므로 스택트레이스를 만들지 않는다(생성 비용 절감).
 * */

public class AuthException extends RuntimeException {

    private final AuthFailure failure;

    private AuthException(AuthFailure failure) {
        super(failure.name(), null, false, false);   // 스택트레이스 생략
        this.failure = failure;
    }

    public AuthFailure getFailure() {
        return failure;
    }

    // 이메일 없음 또는 비밀번호 불일치 (둘을 구분하지 않아 계정 존재 여부가 노출되지 않는다)
    public static AuthException loginFailed() { return new AuthException(AuthFailure.LOGIN_FAILED); }

    // 구글로만 가입한 계정으로 이메일 로그인을 시도한 경우
    public static AuthException socialAccountNoPassword() { return new AuthException(AuthFailure.SOCIAL_ACCOUNT_NO_PASSWORD); }

    // 탈퇴한 계정으로 로그인을 시도한 경우
    public static AuthException withdrawnMember() { return new AuthException(AuthFailure.WITHDRAWN_MEMBER); }

    // 로그인 실패 횟수 초과로 잠긴 경우
    public static AuthException accountLocked() { return new AuthException(AuthFailure.ACCOUNT_LOCKED); }

    // 로그아웃 요청에 Authorization 헤더가 없는 경우
    public static AuthException missingAccessToken() { return new AuthException(AuthFailure.MISSING_ACCESS_TOKEN); }

    // Authorization 헤더 형식이 잘못된 경우
    public static AuthException invalidToken() { return new AuthException(AuthFailure.INVALID_TOKEN); }

    // refresh token 쿠키가 없는 경우
    public static AuthException missingRefreshToken() { return new AuthException(AuthFailure.MISSING_REFRESH_TOKEN); }

    // refresh token이 위조/만료됐거나 Redis 저장값과 다른 경우
    public static AuthException invalidRefreshToken() { return new AuthException(AuthFailure.INVALID_REFRESH_TOKEN); }

    // 이미 가입된 이메일로 인증번호를 요청한 경우
    public static AuthException emailAlreadyRegistered() { return new AuthException(AuthFailure.EMAIL_ALREADY_REGISTERED); }

    // 같은 이메일로 재전송 대기시간(60초) 안에 다시 요청한 경우
    public static AuthException emailSendTooFast() { return new AuthException(AuthFailure.EMAIL_SEND_TOO_FAST); }

    // 같은 IP의 요청 횟수가 제한을 넘은 경우
    public static AuthException emailSendLimitExceeded() { return new AuthException(AuthFailure.EMAIL_SEND_LIMIT_EXCEEDED); }

    // 메일 서버 오류로 발송하지 못한 경우
    public static AuthException emailSendFailed() { return new AuthException(AuthFailure.EMAIL_SEND_FAILED); }

    // 인증번호가 만료됐거나 요청한 적이 없는 경우
    public static AuthException emailCodeExpired() { return new AuthException(AuthFailure.EMAIL_CODE_EXPIRED); }

    // 인증번호가 틀린 경우
    public static AuthException emailCodeMismatch() { return new AuthException(AuthFailure.EMAIL_CODE_MISMATCH); }

    // 인증번호를 여러 번 틀려서 잠긴 경우
    public static AuthException emailVerifyLocked() { return new AuthException(AuthFailure.EMAIL_VERIFY_LOCKED); }

    // 닉네임 규칙에 맞지 않는 경우
    public static AuthException nicknameInvalid() { return new AuthException(AuthFailure.NICKNAME_INVALID); }

    // 같은 IP의 닉네임 확인 요청이 제한을 넘은 경우
    public static AuthException nicknameCheckLimitExceeded() { return new AuthException(AuthFailure.NICKNAME_CHECK_LIMIT_EXCEEDED); }

    // 국세청 정보와 사업자번호, 개업일자, 대표자명이 일치하지 않음
    public static AuthException businessMismatch() {return new AuthException(AuthFailure.BUSINESS_MISMATCH);}

    // 일치하지만 폐업 상태 (휴업은 허용하므로 여기에 오지 않는다)
    public static AuthException businessClosed() {return new AuthException(AuthFailure.BUSINESS_CLOSED);}

    // 이미 다른 회원이 인증한 사업자번호
    public static AuthException businessAlreadyUsed() {return new AuthException(AuthFailure.BUSINESS_ALREADY_USED);}

    // IP당 요청 제한(10분 10회) 초과
    public static AuthException businessVerifyLimitExceeded() {return new AuthException(AuthFailure.BUSINESS_VERIFY_LIMIT_EXCEEDED);}

    // 국세청 호출 실패, 타임아웃, 5xx, 비정상 응답
    public static AuthException businessVerifyUnavailable() {return new AuthException(AuthFailure.BUSINESS_VERIFY_UNAVAILABLE);}

    // 이메일 인증 토큰이 없거나 만료됨 (인증을 다시 해야 함)
    public static AuthException emailTokenInvalid() {return new AuthException(AuthFailure.EMAIL_TOKEN_INVALID);}

    // 사업자 확인 토큰이 없거나 만료됨 (사업자 인증을 다시 해야 함)
    public static AuthException businessTokenInvalid() {return new AuthException(AuthFailure.BUSINESS_TOKEN_INVALID);}

    // 사업자 인증 후 이름을 바꿈 (이름은 대표자명과 같아야 함)
    public static AuthException businessNameMismatch() {return new AuthException(AuthFailure.BUSINESS_NAME_MISMATCH);}

    // 이미 사용 중인 닉네임 (중복 확인 뒤 다른 사람이 먼저 가입한 경우 포함)
    public static AuthException nicknameAlreadyUsed() {return new AuthException(AuthFailure.NICKNAME_ALREADY_USED);}

    // 구글 ID 토큰이 올바르지 않거나 만료됨
    public static AuthException googleTokenInvalid() {return new AuthException(AuthFailure.GOOGLE_TOKEN_INVALID);}

    // 구글이 이메일을 인증하지 않은 계정
    public static AuthException googleEmailUnverified() {return new AuthException(AuthFailure.GOOGLE_EMAIL_UNVERIFIED);}

    // 같은 이메일이 자체 가입 계정으로 이미 있음 (이메일로 로그인해야 함)
    public static AuthException localAccountExists() {return new AuthException(AuthFailure.LOCAL_ACCOUNT_EXISTS);}

    // 구글 검증 실패가 반복되어 잠시 잠금
    public static AuthException googleTooManyAttempts() {return new AuthException(AuthFailure.GOOGLE_TOO_MANY_ATTEMPTS);}

    // 구글 가입 티켓이 없거나 만료됨 (구글 로그인을 다시 해야 함)
    public static AuthException googleTicketInvalid() {return new AuthException(AuthFailure.GOOGLE_TICKET_INVALID);}

}
