package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.controller;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.NicknameCheckCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.BusinessVerifyResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.EmailSendResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.EmailVerifyResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.result.NicknameCheckResult;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.usecase.SignupCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.request.BusinessVerifyRequest;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.request.EmailSendRequest;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.request.EmailVerifyRequest;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.request.SignupRequest;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.response.BusinessVerifyResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.response.EmailSendResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.response.EmailVerifyResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.response.NicknameCheckResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.common.AuthResponseCode;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.common.AuthResponseMessage;
import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.global.web.ClientIpResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    /*
     * comment.
     *  사업자 인증
     *  국세청 진위확인이 통과하면 가입 요청에 넣을 businessToken을 내려준다. (유효 30분, 가입 성공 시 1회용으로 소멸)
     *  IP 제한을 위해 클라이언트 IP를 같이 넘긴다.
     * */
    @PostMapping("/business/verify")
    public ResponseEntity<ApiResponse<BusinessVerifyResponse>> verifyBusiness(@RequestBody @Valid BusinessVerifyRequest request,
                                                                              HttpServletRequest httpRequest) {
        BusinessVerifyResult result = signupCommandUsecase.verifyBusiness(
                request.toCommand(ClientIpResolver.resolve(httpRequest)));

        return ResponseEntity.ok(ApiResponse.success(AuthResponseCode.BUSINESS_VERIFIED,
                AuthResponseMessage.BUSINESS_VERIFIED, BusinessVerifyResponse.from(result)));
    }

    /*
     * comment.
     *  자체 회원가입
     *  성공하면 본문 없이 201만 내려준다. (프론트가 로그인 화면으로 이동하고, 토큰은 발급하지 않는다)
     * */
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest request) {
        signupCommandUsecase.signup(request.toCommand());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
