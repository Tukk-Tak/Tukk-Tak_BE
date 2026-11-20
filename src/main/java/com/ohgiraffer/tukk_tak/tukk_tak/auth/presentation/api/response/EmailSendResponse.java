package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.response;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.EmailSendResult;

/*
 * comment.
 *  이메일 인증번호 발송 응답 (프론트가 타이머와 재전송 버튼 활성화에 사용)
 * */

public record EmailSendResponse(
        long expiresInSeconds,     // 인증번호 유효시간(초): 300
        long resendAfterSeconds    // 재전송 가능해질 때까지(초): 60
) {
    public static EmailSendResponse from(EmailSendResult result) {
        return new EmailSendResponse(result.expiresInSeconds(), result.resendAfterSeconds());
    }
}
