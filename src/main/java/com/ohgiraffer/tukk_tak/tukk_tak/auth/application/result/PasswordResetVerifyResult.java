package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result;

/*
 * comment.
 *  비밀번호 재설정 인증번호 검증 결과
 * */

public record PasswordResetVerifyResult(
        String resetToken    // 새 비밀번호 설정 요청에 넣을 재설정 토큰
) {
}
