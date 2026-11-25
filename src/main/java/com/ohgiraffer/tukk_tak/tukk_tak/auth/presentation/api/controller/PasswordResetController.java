package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.controller;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.EmailSendResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.PasswordResetVerifyResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.usecase.PasswordResetUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.common.AuthResponseCode;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.common.AuthResponseMessage;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.request.EmailSendRequest;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.request.EmailVerifyRequest;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.request.PasswordResetRequest;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.response.EmailSendResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.response.PasswordResetVerifyResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.global.web.ClientIpResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * comment.
 *  비밀번호 재설정 API 컨트롤러 (/auth/password/reset/...)
 *  로그인 전에 쓰는 API라서 인증 없이 호출한다. 요청과 응답 형식은 회원가입 이메일 인증과 같다.
 * */

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/password/reset")
public class PasswordResetController {

    private final PasswordResetUsecase passwordResetUsecase;

    /*
     * comment.
     *  재설정 인증번호 발송
     *  성공하면 유효시간(300초)과 재전송 대기시간(60초)을 내려준다.
     * */
    @PostMapping("/email/send")
    public ResponseEntity<ApiResponse<EmailSendResponse>> sendResetCode(@RequestBody @Valid EmailSendRequest request,
                                                                        HttpServletRequest httpRequest) {
        EmailSendResult result = passwordResetUsecase.sendResetCode(
                request.toCommand(ClientIpResolver.resolve(httpRequest)));

        return ResponseEntity.ok(ApiResponse.success(AuthResponseCode.EMAIL_SENT,
                AuthResponseMessage.EMAIL_SENT, EmailSendResponse.from(result)));
    }

    /*
     * comment.
     *  재설정 인증번호 검증
     *  맞으면 새 비밀번호 설정에 쓸 resetToken을 내려준다 (유효 10분, 비밀번호를 바꾸면 1회용으로 소멸).
     * */
    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<PasswordResetVerifyResponse>> verifyResetCode(
            @RequestBody @Valid EmailVerifyRequest request) {
        PasswordResetVerifyResult result = passwordResetUsecase.verifyResetCode(request.toCommand());

        return ResponseEntity.ok(ApiResponse.success(AuthResponseCode.EMAIL_VERIFIED,
                AuthResponseMessage.EMAIL_VERIFIED, PasswordResetVerifyResponse.from(result)));
    }

    /*
     * comment.
     *  새 비밀번호 설정
     *  성공하면 본문 없이 200만 내려준다. (자동 로그인은 하지 않고, 프론트가 로그인 화면으로 이동한다)
     * */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody @Valid PasswordResetRequest request) {
        passwordResetUsecase.resetPassword(request.toCommand());

        return ResponseEntity.ok(ApiResponse.success(AuthResponseCode.PASSWORD_RESET,
                AuthResponseMessage.PASSWORD_RESET));
    }

}
