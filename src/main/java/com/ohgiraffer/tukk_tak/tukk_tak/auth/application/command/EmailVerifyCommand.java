package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command;

/*
 * comment.
 *  이메일 인증번호 검증 명령
 * */

public record EmailVerifyCommand(
        String email,   // 인증번호를 받은 이메일
        String code     // 사용자가 입력한 6자리 인증번호
) {
}
