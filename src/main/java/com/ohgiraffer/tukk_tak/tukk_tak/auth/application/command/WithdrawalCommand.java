package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command;

/*
 * comment.
 *  회원 탈퇴 명령 (로그인한 회원이 마이페이지에서 요청한다)
 *  자체 가입 회원은 password, 구글 가입 회원은 credential로 본인을 확인한다.
 * */

public record WithdrawalCommand(
        Long memberId,        // 로그인한 회원 (토큰에서 꺼낸 값)
        String password,      // 현재 비밀번호 (자체 가입 회원, 평문)
        String credential     // 새로 받은 구글 ID 토큰 (구글 가입 회원)
) {
}
