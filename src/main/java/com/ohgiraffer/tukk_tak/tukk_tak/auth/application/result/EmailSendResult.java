package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result;

/*
 * comment.
 *  이메일 인증번호 발송 결과 (프론트 타이머와 재전송 버튼에 쓰는 값)
 * */

public record EmailSendResult(
        long expiresInSeconds,     // 인증번호 유효시간(초)
        long resendAfterSeconds    // 재전송 가능해질 때까지의 시간(초)
) {
}
