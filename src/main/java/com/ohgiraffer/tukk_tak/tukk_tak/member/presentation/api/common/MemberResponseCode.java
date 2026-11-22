package com.ohgiraffer.tukk_tak.tukk_tak.member.presentation.api.common;

/*
 * comment.
 *  회원 도메인의 응답 코드 상수 (MEMBER-로 시작)
 * */

public final class MemberResponseCode {

    private MemberResponseCode() {}

    // 성공
    public static final String MY_INFO_FETCHED = "MEMBER-MY-INFO-FETCHED";
    public static final String MY_INFO_UPDATED = "MEMBER-MY-INFO-UPDATED";

    // 실패
    public static final String NICKNAME_ALREADY_USED = "MEMBER-NICKNAME-ALREADY-USED";

}
