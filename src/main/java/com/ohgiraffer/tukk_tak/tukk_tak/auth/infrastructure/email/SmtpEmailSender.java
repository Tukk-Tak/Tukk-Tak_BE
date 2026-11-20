package com.ohgiraffer.tukk_tak.tukk_tak.auth.infrastructure.email;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port.EmailSender;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.exception.AuthException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/*
 * comment.
 *  SMTP(Gmail)로 인증 메일을 보내는 구현체
 *  동기로 보내므로 실패하면 바로 AuthException.emailSendFailed()를 던져서 503으로 응답한다.
 *  (타임아웃은 application.yaml의 spring.mail.properties에서 5초로 제한)
 *  인증번호는 보안상 로그에 남기지 않는다.
 * */

@Slf4j
@Component
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender mailSender;

    @Override
    public void sendVerificationCode(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("[뚝딱] 이메일 인증번호 안내");
            helper.setText(buildTemplate(code), true);   // true = HTML
            mailSender.send(message);
        } catch (Exception e) {
            // 원인은 서버 로그에만 남기고, 사용자에게는 일반 문구만 보여준다
            log.error("[email] 인증 메일 발송 실패", e);
            throw AuthException.emailSendFailed();
        }
    }

    // HTML 본문 (인증번호 6자리를 크게 보여준다)
    private String buildTemplate(String code) {
        return """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 40px 20px; background-color: #f9f9f9;">
                <div style="background-color: #ffffff; border-radius: 8px; padding: 40px;">
                    <h1 style="color: #333333; font-size: 24px; margin-bottom: 8px;">뚝딱</h1>
                    <hr style="border: none; border-top: 2px solid #f0f0f0; margin: 20px 0;">
                    <p style="color: #555555; font-size: 16px;">아래 인증번호를 입력해주세요.</p>
                    <div style="background-color: #f4f4f4; border-radius: 6px; padding: 20px; text-align: center; margin: 30px 0;">
                        <span style="font-size: 36px; font-weight: bold; letter-spacing: 8px; color: #222222;">%s</span>
                    </div>
                    <p style="color: #888888; font-size: 14px;">인증번호는 5분 동안만 유효합니다.</p>
                    <p style="color: #888888; font-size: 14px;">본인이 요청하지 않았다면 이 메일을 무시해주세요.</p>
                </div>
            </div>
            """.formatted(code);
    }

}
