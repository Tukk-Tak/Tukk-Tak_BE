package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.request;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.PasswordChangeCommand;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/*
 * comment.
 *  비밀번호 변경 요청
 *  새 비밀번호 규칙은 가입, 재설정과 같고(PasswordRules), 형식이 틀리면 컨트롤러에 들어오기 전에 400으로 걸러낸다.
 * */

public record PasswordChangeRequest(
        // 관리자 비밀번호가 16자를 넘을 수 있어서 로그인과 같은 상한(64)만 둔다 (규칙 검사는 새 비밀번호에만 한다)
        @NotBlank(message = "현재 비밀번호를 입력해주세요.")
        @Size(max = 64, message = "비밀번호가 너무 깁니다.")
        String currentPassword,

        // 영문, 숫자, 특수문자를 모두 포함한 8~16자 (공백 불가)
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

    // 요청 DTO -> 명령 객체 (회원 id는 요청 본문이 아니라 토큰에서 꺼낸 값을 쓴다)
    public PasswordChangeCommand toCommand(Long memberId) {
        return new PasswordChangeCommand(memberId, currentPassword, newPassword);
    }
}
