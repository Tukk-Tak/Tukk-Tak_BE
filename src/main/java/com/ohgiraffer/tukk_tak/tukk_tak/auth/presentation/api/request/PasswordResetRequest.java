package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.request;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.PasswordResetCommand;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/*
 * comment.
 *  비밀번호 재설정 요청
 *  형식이 틀리면 컨트롤러에 들어오기 전에 400(COMMON-VALIDATION-ERROR)으로 응답된다.
 * */

public record PasswordResetRequest(
        @NotBlank(message = "비밀번호 재설정 인증이 필요합니다.")
        String resetToken,

        @NotBlank(message = "새 비밀번호를 입력해주세요.")
        @Pattern(regexp = PasswordRules.REGEX, message = PasswordRules.MESSAGE)
        String newPassword,

        @NotBlank(message = "새 비밀번호 확인을 입력해주세요.")
        String newPasswordConfirm
) {
    // 새 비밀번호와 확인값 비교 (필드 두 개를 비교하는 검증이라 메서드에 @AssertTrue를 붙였다)
    @AssertTrue(message = "새 비밀번호와 비밀번호 확인이 일치하지 않습니다.")
    public boolean isPasswordConfirmed() {
        return newPassword != null && newPassword.equals(newPasswordConfirm);
    }

    // 요청 DTO -> 명령 객체
    public PasswordResetCommand toCommand() {
        return new PasswordResetCommand(resetToken, newPassword);
    }
}
