package com.ohgiraffer.tukk_tak.tukk_tak.member.presentation.api.common;

/*
 * comment.
 *  회원 도메인의 응답 코드 상수 (MEMBER-로 시작)
 * */

public final class MemberResponseCode {

    private MemberResponseCode() {}

    // 성공
    public static final String MY_INFO_FETCHED   = "MEMBER-MY-INFO-FETCHED";
    public static final String MY_INFO_UPDATED   = "MEMBER-MY-INFO-UPDATED";
    public static final String BUSINESS_VERIFIED = "MEMBER-BUSINESS-VERIFIED";

    // 실패
    public static final String NICKNAME_ALREADY_USED          = "MEMBER-NICKNAME-ALREADY-USED";
    public static final String BUSINESS_MISMATCH              = "MEMBER-BUSINESS-MISMATCH";
    public static final String BUSINESS_CLOSED                = "MEMBER-BUSINESS-CLOSED";
    public static final String BUSINESS_ALREADY_USED          = "MEMBER-BUSINESS-ALREADY-USED";
    public static final String BUSINESS_VERIFY_LIMIT_EXCEEDED = "MEMBER-BUSINESS-VERIFY-LIMIT-EXCEEDED";
    public static final String BUSINESS_VERIFY_UNAVAILABLE    = "MEMBER-BUSINESS-VERIFY-UNAVAILABLE";
    public static final String BUSINESS_TOKEN_INVALID         = "MEMBER-BUSINESS-TOKEN-INVALID";
    public static final String BUSINESS_NAME_MISMATCH         = "MEMBER-BUSINESS-NAME-MISMATCH";
    public static final String BUSINESS_ALREADY_VERIFIED      = "MEMBER-BUSINESS-ALREADY-VERIFIED";

}
