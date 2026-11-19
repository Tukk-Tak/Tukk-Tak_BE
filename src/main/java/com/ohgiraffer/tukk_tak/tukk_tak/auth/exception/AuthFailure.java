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
    INVALID_REFRESH_TOKEN        // refresh token 위조/만료/불일치
}
