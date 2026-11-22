package com.ohgiraffer.tukk_tak.tukk_tak.member.domain.exception;

/*
 * comment.
 *  회원 도메인 전용 예외
 *  - 실패 종류(MemberFailure)만 들고 있고, 상태코드/문구는 MemberExceptionHandler가 정한다.
 *  - 사용자가 자주 만나는 정상 흐름이라 스택트레이스를 만들지 않는다.
 * */

public class MemberException extends RuntimeException {

    private final MemberFailure failure;

    private MemberException(MemberFailure failure) {
        super(failure.name(), null, false, false);   // 스택트레이스 생략
        this.failure = failure;
    }

    public MemberFailure getFailure() {
        return failure;
    }

    // 이미 다른 회원이 쓰는 닉네임으로 바꾸려는 경우
    public static MemberException nicknameAlreadyUsed() { return new MemberException(MemberFailure.NICKNAME_ALREADY_USED); }

    // 국세청 정보와 사업자번호, 개업일자, 대표자명이 일치하지 않음
    public static MemberException businessMismatch() {return new MemberException(MemberFailure.BUSINESS_MISMATCH);}

    // 일치하지만 폐업 상태 (휴업은 허용하므로 여기에 오지 않는다)
    public static MemberException businessClosed() {return new MemberException(MemberFailure.BUSINESS_CLOSED);}

    // 이미 다른 회원이 인증한 사업자번호
    public static MemberException businessAlreadyUsed() {return new MemberException(MemberFailure.BUSINESS_ALREADY_USED);}

    // 요청 횟수 제한 초과
    public static MemberException businessVerifyLimitExceeded() {return new MemberException(MemberFailure.BUSINESS_VERIFY_LIMIT_EXCEEDED);}

    // 국세청 호출 실패, 타임아웃, 5xx, 비정상 응답
    public static MemberException businessVerifyUnavailable() {return new MemberException(MemberFailure.BUSINESS_VERIFY_UNAVAILABLE);}

    // 사업자 확인 토큰이 없거나 만료됨 (사업자 인증을 다시 해야 함)
    public static MemberException businessTokenInvalid() {return new MemberException(MemberFailure.BUSINESS_TOKEN_INVALID);}

    // 사업자 인증 후 이름을 바꿈 (이름은 대표자명과 같아야 함)
    public static MemberException businessNameMismatch() {return new MemberException(MemberFailure.BUSINESS_NAME_MISMATCH);}

}
