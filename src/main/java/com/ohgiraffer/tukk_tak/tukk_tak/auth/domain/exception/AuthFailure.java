package com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.exception;

/*
 * comment.
 *  인증 도메인에서 발생하는 실패 종류 ENUM
 *  HTTP 상태, 응답 코드, 문구는 모르고, presentation의 AuthExceptionHandler가 변환한다.
 * */

public enum AuthFailure {
    LOGIN_FAILED,                // 이메일 없음 또는 비밀번호 불일치
    SOCIAL_ACCOUNT_NO_PASSWORD,  // 구글로만 가입해서 비밀번호가 없는 계정
    WITHDRAWN_MEMBER,            // 탈퇴한 계정
    ACCOUNT_LOCKED,              // 로그인 실패 횟수 초과로 잠긴 계정
    MISSING_ACCESS_TOKEN,        // Authorization 헤더 없음
    INVALID_TOKEN,               // Authorization 헤더 형식 오류
    MISSING_REFRESH_TOKEN,       // refresh token 쿠키 없음
    INVALID_REFRESH_TOKEN,       // refresh token 위조/만료/불일치
    EMAIL_ALREADY_REGISTERED,    // 이미 가입된 이메일로 인증번호를 요청
    EMAIL_SEND_TOO_FAST,         // 같은 이메일로 60초 안에 재요청
    EMAIL_SEND_LIMIT_EXCEEDED,   // 같은 IP에서 요청이 너무 많음
    EMAIL_SEND_FAILED,           // 메일 서버 오류로 발송 실패
    EMAIL_CODE_EXPIRED,          // 인증번호가 만료됐거나 요청한 적이 없음
    EMAIL_CODE_MISMATCH,         // 인증번호가 틀림
    EMAIL_VERIFY_LOCKED,         // 인증번호를 여러 번 틀려서 잠김
    NICKNAME_INVALID,            // 닉네임 규칙(한글·영문·숫자 2~15자)에 맞지 않음
    NICKNAME_CHECK_LIMIT_EXCEEDED,// 같은 IP의 닉네임 확인 요청이 너무 많음
    BUSINESS_MISMATCH,                // 국세청 정보와 불일치 (400)
    BUSINESS_CLOSED,                  // 폐업 사업자 (400)
    BUSINESS_ALREADY_USED,            // 이미 다른 회원이 인증한 사업자번호 (409)
    BUSINESS_VERIFY_LIMIT_EXCEEDED,   // IP당 요청 횟수 초과 (429)
    BUSINESS_VERIFY_UNAVAILABLE,      // 국세청 장애, 타임아웃, 비정상 응답 (503)
    EMAIL_TOKEN_INVALID,       // 이메일 인증 토큰이 없거나 만료 (400)
    BUSINESS_TOKEN_INVALID,    // 사업자 확인 토큰이 없거나 만료 (400)
    BUSINESS_NAME_MISMATCH,    // 가입 이름이 사업자 대표자명과 다름 (400)
    NICKNAME_ALREADY_USED,     // 이미 사용 중인 닉네임 (409)
    GOOGLE_TOKEN_INVALID,         // 구글 ID 토큰이 올바르지 않거나 만료 (401)
    GOOGLE_EMAIL_UNVERIFIED,      // 구글이 이메일을 인증하지 않은 계정 (400)
    LOCAL_ACCOUNT_EXISTS,         // 같은 이메일이 자체 가입으로 이미 있음 (409)
    GOOGLE_TOO_MANY_ATTEMPTS,     // 구글 검증 실패가 반복되어 잠금 (429)
    GOOGLE_TICKET_INVALID,        // 구글 가입 티켓이 없거나 만료 (400)
    EMAIL_NOT_REGISTERED,          // 가입되지 않은 이메일로 비밀번호 재설정을 요청 (400)
    PASSWORD_RESET_SOCIAL_ACCOUNT, // 구글 회원은 비밀번호가 없어 재설정할 수 없음 (400)
    PASSWORD_RESET_WITHDRAWN,      // 탈퇴한 회원은 재설정할 수 없음 (400)
    PASSWORD_RESET_TOKEN_INVALID,  // 재설정 토큰이 없거나 만료됐거나, 그 사이 회원 상태가 바뀜 (400)
    PASSWORD_SAME_AS_CURRENT,      // 새 비밀번호가 현재 비밀번호와 같음 (400)
    CURRENT_PASSWORD_MISMATCH,         // 비밀번호 변경 때 현재 비밀번호가 틀림 (400)
    PASSWORD_CHANGE_LOCKED,            // 현재 비밀번호를 여러 번 틀려서 변경이 잠김 (429)
    PASSWORD_CHANGE_SOCIAL_ACCOUNT,    // 구글 회원은 비밀번호가 없어 변경할 수 없음 (400)
}
