package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command;

/*
 * comment.
 *  비밀번호 재설정 명령
 * */

public record PasswordResetCommand(
        String resetToken,     // 인증번호 검증 때 받은 재설정 토큰
        String newPassword     // 새 비밀번호 (평문, 서비스가 암호화해서 저장)
) {
}
