package com.ohgiraffer.tukk_tak.tukk_tak.auth.exception;

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
}
