package com.ohgiraffer.tukk_tak.tukk_tak.member.domain.exception;

/*
 * comment.
 *  회원 도메인에서 발생하는 실패 종류 ENUM
 *  HTTP 상태, 응답 코드, 문구는 모르고, presentation의 MemberExceptionHandler가 변환한다.
 * */

public enum MemberFailure {
    NICKNAME_ALREADY_USED,   // 이미 사용 중인 닉네임 (409)
    BUSINESS_MISMATCH,                // 국세청 정보와 불일치 (400)
    BUSINESS_CLOSED,                  // 폐업 사업자 (400)
    BUSINESS_ALREADY_USED,            // 이미 다른 회원이 인증한 사업자번호 (409)
    BUSINESS_VERIFY_LIMIT_EXCEEDED,   // 요청 횟수 초과 (429)
    BUSINESS_VERIFY_UNAVAILABLE,      // 국세청 장애, 타임아웃, 비정상 응답 (503)
    BUSINESS_TOKEN_INVALID,           // 사업자 확인 토큰이 없거나 만료 (400)
    BUSINESS_NAME_MISMATCH,           // 가입 이름이 사업자 대표자명과 다름 (400)
}
