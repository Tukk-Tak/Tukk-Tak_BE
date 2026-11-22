package com.ohgiraffer.tukk_tak.tukk_tak.member.domain.exception;

/*
 * comment.
 *  회원 도메인에서 발생하는 실패 종류 ENUM
 *  HTTP 상태, 응답 코드, 문구는 모르고, presentation의 MemberExceptionHandler가 변환한다.
 * */

public enum MemberFailure {
    NICKNAME_ALREADY_USED   // 이미 사용 중인 닉네임 (409)
}
