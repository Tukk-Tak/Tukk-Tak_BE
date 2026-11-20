package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command;

/*
 * comment.
 *  이메일 인증번호 발송 명령
 * */

public record EmailSendCommand(
        String email,      // 인증번호를 받을 이메일
        String clientIp    // 요청한 클라이언트 IP (IP별 발송 제한에 사용)
) {
}
