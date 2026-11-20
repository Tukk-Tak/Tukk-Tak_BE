package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command;

/*
 * comment.
 *  닉네임 중복 확인 명령
 * */

public record NicknameCheckCommand(
        String nickname,   // 확인할 닉네임
        String clientIp    // 요청한 클라이언트 IP (IP별 요청 제한에 사용)
) {
}
