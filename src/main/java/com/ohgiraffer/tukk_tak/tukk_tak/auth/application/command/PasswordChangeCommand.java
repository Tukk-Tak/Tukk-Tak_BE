package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command;

/*
 * comment.
 *  비밀번호 변경 명령 (로그인한 회원이 마이페이지에서 바꾼다)
 * */

public record PasswordChangeCommand(
        Long memberId,            // 로그인한 회원 (토큰에서 꺼낸 값)
        String currentPassword,   // 현재 비밀번호 (평문)
        String newPassword        // 새 비밀번호 (평문, 서비스가 암호화해서 저장)
) {
}
