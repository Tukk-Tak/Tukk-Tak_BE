package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.NicknameCheckCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.EmailSendResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.EmailVerifyResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.NicknameCheckResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.usecase.SignupCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.request.EmailSendRequest;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.request.EmailVerifyRequest;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.response.EmailSendResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.response.EmailVerifyResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.response.NicknameCheckResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.response.AuthResponseCode;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.response.AuthResponseMessage;
import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.global.web.ClientIpResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 * comment.
 *  회원가입 API 컨트롤러 (/auth/email/send, 이후 닉네임·사업자·가입 API가 추가된다)
 * */

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SignupController {

    private final SignupCommandUsecase signupCommandUsecase;

    /*
     * comment.
     *  이메일 인증번호 발송
     *  성공하면 유효시간(300초)과 재전송 대기시간(60초)을 내려준다.
     * */
    @PostMapping("/email/send")
    public ResponseEntity<ApiResponse<EmailSendResponse>> sendEmailCode(@RequestBody @Valid EmailSendRequest request,
                                                                        HttpServletRequest httpRequest) {
        EmailSendResult result = signupCommandUsecase.sendEmailCode(
                request.toCommand(ClientIpResolver.resolve(httpRequest)));

        return ResponseEntity.ok(ApiResponse.success(AuthResponseCode.EMAIL_SENT,
                AuthResponseMessage.EMAIL_SENT, EmailSendResponse.from(result)));
    }

    /*
     * comment.
     *  이메일 인증번호 검증
     *  맞으면 가입 요청에 넣을 emailToken을 내려준다 (유효 30분, 가입 성공 시 1회용으로 소멸).
     * */
    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<EmailVerifyResponse>> verifyEmailCode(@RequestBody @Valid EmailVerifyRequest request) {
        EmailVerifyResult result = signupCommandUsecase.verifyEmailCode(request.toCommand());

        return ResponseEntity.ok(ApiResponse.success(AuthResponseCode.EMAIL_VERIFIED,
                AuthResponseMessage.EMAIL_VERIFIED, EmailVerifyResponse.from(result)));
    }

    /*
     * comment.
     *  닉네임 중복 확인
     *  이미 사용 중이어도 오류가 아니라 available=false로 응답한다 (규칙 위반만 400).
     *  required = false: 파라미터가 아예 없는 요청도 서비스의 규칙 검사에서 400으로 처리되게 한다.
     * */
    @GetMapping("/nickname/check")
    public ResponseEntity<ApiResponse<NicknameCheckResponse>> checkNickname(
            @RequestParam(required = false) String nickname,
            HttpServletRequest httpRequest) {
        NicknameCheckResult result = signupCommandUsecase.checkNickname(
                new NicknameCheckCommand(nickname, ClientIpResolver.resolve(httpRequest)));

        return ResponseEntity.ok(ApiResponse.success(AuthResponseCode.NICKNAME_CHECKED,
                AuthResponseMessage.NICKNAME_CHECKED, NicknameCheckResponse.from(result)));
    }

}
