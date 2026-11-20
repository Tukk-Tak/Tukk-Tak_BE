package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result;

/*
 * comment.
 *  이메일 인증번호 검증 결과
 * */

public record EmailVerifyResult(
        String emailToken    // 가입 요청에 넣을 이메일 인증 토큰
) {
}
