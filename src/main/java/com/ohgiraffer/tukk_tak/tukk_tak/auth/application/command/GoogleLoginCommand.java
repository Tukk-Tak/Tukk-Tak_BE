package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command;

/*
 * comment.
 *  구글 로그인 명령
 * */

public record GoogleLoginCommand(
        String credential,    // 프론트가 구글에서 받은 ID 토큰
        String clientIp       // 컨트롤러가 요청에서 구한 IP (실패 횟수 제한용)
) {
}
