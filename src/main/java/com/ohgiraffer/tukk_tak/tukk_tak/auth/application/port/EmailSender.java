package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port;

/*
 * comment.
 *  인증 메일 발송 포트
 *  지금은 Gmail SMTP로 보내지만, 나중에 AWS SES로 바꿀 때 이 인터페이스의 구현체만 교체한다.
 * */

public interface EmailSender {

    // 인증번호 메일을 보낸다 (발송에 실패하면 AuthException.emailSendFailed()를 던진다)
    void sendVerificationCode(String toEmail, String code);

    // 비밀번호 재설정 인증번호 메일을 보낸다 (발송에 실패하면 AuthException.emailSendFailed()를 던진다)
    void sendPasswordResetCode(String toEmail, String code);

}
