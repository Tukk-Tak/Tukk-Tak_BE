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

}
