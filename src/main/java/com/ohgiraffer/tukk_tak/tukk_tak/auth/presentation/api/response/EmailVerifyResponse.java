package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.response;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.EmailVerifyResult;

/*
 * comment.
 *  이메일 인증번호 검증 응답
 *  프론트는 emailToken을 보관했다가 회원가입 요청(/auth/signup)에 그대로 넣어 보낸다.
 * */

public record EmailVerifyResponse(
        String emailToken
) {
    public static EmailVerifyResponse from(EmailVerifyResult result) {
        return new EmailVerifyResponse(result.emailToken());
    }
}
