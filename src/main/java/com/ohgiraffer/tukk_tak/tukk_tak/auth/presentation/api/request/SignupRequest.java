package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.request;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.SignupCommand;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/*
 * comment.
 *  자체 회원가입 요청
 *  형식이 틀리면 컨트롤러에 들어오기 전에 400(COMMON-VALIDATION-ERROR)으로 응답된다.
 *  전화번호는 하이픈이 있어도 없어도 받고, 서버가 숫자만 남겨서 저장한다.
 * */

public record SignupRequest(
        @NotBlank(message = "이메일 인증이 필요합니다.")
        String emailToken,

        // 영문, 숫자, 특수문자를 모두 포함한 8~16자 (공백 불가)
        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9])\\S{8,16}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함한 8~16자로 입력해주세요.")
        String password,

        @NotBlank(message = "비밀번호 확인을 입력해주세요.")
        String passwordConfirm,

        @NotBlank(message = "이름을 입력해주세요.")
        @Size(max = 50, message = "이름은 50자 이하로 입력해주세요.")
        String name,

        // 닉네임 확인 API와 같은 규칙 (한글, 영문, 숫자 2~15자)
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,15}$", message = "닉네임은 한글, 영문, 숫자 2~15자로 입력해주세요.")
        String nickname,

        // 01로 시작하는 10~11자리 (하이픈 허용)
        @NotBlank(message = "전화번호를 입력해주세요.")
        @Pattern(regexp = "^01\\d-?\\d{3,4}-?\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
        String phone,

        // 개인정보 수집·이용과 AI 사용 동의는 체크박스 하나이고, 반드시 true여야 한다
        @AssertTrue(message = "개인정보 수집·이용 및 AI 사용에 동의해주세요.")
        boolean agreed,

        // 아래 세 개는 선택 항목 (사업자 인증을 했을 때만 businessToken을 보낸다)
        String businessToken,

        @Pattern(regexp = "^(|\\d{2,4}-?\\d{3,4}-?\\d{4})$", message = "업장 전화번호 형식이 올바르지 않습니다.")
        String businessPhone,

        @Size(max = 255, message = "주소는 255자 이하로 입력해주세요.")
        String address
) {
    // 비밀번호와 확인값 비교 (필드 두 개를 비교하는 검증이라 메서드에 @AssertTrue를 붙였다)
    @AssertTrue(message = "비밀번호와 비밀번호 확인이 일치하지 않습니다.")
    public boolean isPasswordConfirmed() {
        return password != null && password.equals(passwordConfirm);
    }

    // 요청 DTO -> 명령 객체 (이름과 닉네임은 앞뒤 공백을 제거)
    public SignupCommand toCommand() {
        return new SignupCommand(emailToken, password, name.trim(), nickname.trim(), phone,
                agreed, businessToken, businessPhone, address);
    }
}
