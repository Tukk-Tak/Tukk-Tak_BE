package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.request;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.EmailSendCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
 * comment.
 *  이메일 인증번호 발송 요청
 *  형식이 틀리면 컨트롤러에 들어오기 전에 400(COMMON-VALIDATION-ERROR)으로 응답된다.
 *  max=100은 member.email 컬럼(VARCHAR(100))에 맞춘 값이다.
 * */

public record EmailSendRequest(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Size(max = 100, message = "이메일은 100자 이하로 입력해주세요.")
        String email
) {
    // 요청 DTO -> 명령 객체 (IP는 컨트롤러가 헤더에서 구해서 넘긴다)
    public EmailSendCommand toCommand(String clientIp) {
        return new EmailSendCommand(email, clientIp);
    }
}
