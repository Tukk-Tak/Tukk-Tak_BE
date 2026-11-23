package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.request;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.GoogleLoginCommand;
import jakarta.validation.constraints.NotBlank;

/*
 * comment.
 *  구글 로그인 요청 (프론트가 구글에서 받은 ID 토큰)
 * */

public record GoogleLoginRequest(
        @NotBlank(message = "구글 인증 정보가 필요합니다.")
        String credential
) {
    // 요청 DTO -> 명령 객체 (clientIp는 컨트롤러가 요청에서 구해서 넘김)
    public GoogleLoginCommand toCommand(String clientIp) {
        return new GoogleLoginCommand(credential, clientIp);
    }
}
