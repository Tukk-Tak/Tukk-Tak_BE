package com.ohgiraffer.tukk_tak.tukk_tak.member.presentation.api.common;

/*
 * comment.
 *  회원 도메인의 응답 메시지 상수
 * */

public final class MemberResponseMessage {

    private MemberResponseMessage() {}

    public static final String MY_INFO_FETCHED = "회원 정보를 조회했어요.";
    public static final String MY_INFO_UPDATED = "회원 정보가 수정되었어요.";
    public static final String BUSINESS_VERIFIED = "사업자 인증이 완료되었습니다.";

    public static final String NICKNAME_ALREADY_USED = "이미 사용 중인 닉네임입니다.";
    public static final String BUSINESS_MISMATCH              = "국세청 정보와 일치하지 않아요. 개업 직후에는 반영까지 며칠 걸릴 수 있어요.";
    public static final String BUSINESS_CLOSED                = "폐업한 사업자는 인증할 수 없습니다.";
    public static final String BUSINESS_ALREADY_USED          = "이미 인증된 사업자등록번호입니다.";
    public static final String BUSINESS_VERIFY_LIMIT_EXCEEDED = "요청이 너무 많습니다. 잠시 후 다시 시도해주세요.";
    public static final String BUSINESS_VERIFY_UNAVAILABLE    = "사업자 인증 서비스가 일시적으로 불안정합니다. 잠시 후 다시 시도해주세요.";
    public static final String BUSINESS_TOKEN_INVALID         = "사업자 인증이 만료되었습니다. 다시 인증해주세요.";
    public static final String BUSINESS_NAME_MISMATCH         = "이름이 사업자 대표자명과 다릅니다. 사업자 인증을 다시 진행해주세요.";

}
