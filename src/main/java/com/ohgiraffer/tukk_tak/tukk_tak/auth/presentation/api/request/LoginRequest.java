package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.request;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.LoginCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
 * comment.
 *  로그인 요청 DTO (이메일, 비밀번호, 로그인 상태 유지 여부)
 * */

public record LoginRequest(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Size(max = 100, message = "이메일은 100자 이내로 입력해주세요.")
        String email,

        // 관리자 비밀번호가 16자를 넘을 수 있어서 상한을 64로 둠 (가입 시 규칙은 가입 API에서 따로 검증)
        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(max = 64, message = "비밀번호가 너무 깁니다.")
        String password,

        // "로그인 상태 유지" 체크박스 값. 값을 안 보내면 false(세션 유지)로 처리된다
        boolean rememberMe
) {
        // presentation 입력을 application 명령 객체로 변환 (clientIp는 컨트롤러가 요청에서 구해서 넘김)
        public LoginCommand toCommand(String clientIp) {
                return new LoginCommand(email, password, rememberMe, clientIp);
        }
}
