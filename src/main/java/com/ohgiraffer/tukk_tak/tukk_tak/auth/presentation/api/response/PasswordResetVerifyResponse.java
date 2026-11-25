package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.response;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.PasswordResetVerifyResult;

/*
 * comment.
 *  비밀번호 재설정 인증번호 검증 응답
 *  프론트는 resetToken을 보관했다가 새 비밀번호 설정 요청(/auth/password/reset)에 그대로 넣어 보낸다.
 * */

public record PasswordResetVerifyResponse(
        String resetToken
) {
    public static PasswordResetVerifyResponse from(PasswordResetVerifyResult result) {
        return new PasswordResetVerifyResponse(result.resetToken());
    }
}
