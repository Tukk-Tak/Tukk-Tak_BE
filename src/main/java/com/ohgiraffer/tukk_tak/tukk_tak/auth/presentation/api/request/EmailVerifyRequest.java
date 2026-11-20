package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.request;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.EmailVerifyCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/*
 * comment.
 *  이메일 인증번호 검증 요청
 *  인증번호는 숫자 6자리만 허용해서, 이상한 값은 컨트롤러에 들어오기 전에 400으로 걸러낸다.
 * */

public record EmailVerifyRequest(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Size(max = 100, message = "이메일은 100자 이하로 입력해주세요.")
        String email,

        @NotBlank(message = "인증번호를 입력해주세요.")
        @Pattern(regexp = "\\d{6}", message = "인증번호는 숫자 6자리입니다.")
        String code
) {
    public EmailVerifyCommand toCommand() {
        return new EmailVerifyCommand(email, code);
    }
}
