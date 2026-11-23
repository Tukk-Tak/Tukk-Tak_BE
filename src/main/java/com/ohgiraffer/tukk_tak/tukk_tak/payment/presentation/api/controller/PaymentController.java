package com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.controller;

import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.user.CustomUserPrincipal;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.PreparePaymentResult;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.usecase.PaymentCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.common.PaymentResponseCode;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.common.PaymentResponseMessage;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.request.PreparePaymentRequest;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.response.PreparePaymentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * comment.
 *  결제 API 컨트롤러 (/payments/prepare)
 *  로그인한 사용자만 호출할 수 있고, 회원 번호는 body가 아니라 토큰에서 꺼낸다.
 * */

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentCommandUsecase paymentCommandUsecase;

    /*
     * comment.
     *  결제 준비
     *  금액과 주문번호를 서버가 확정해서 내려준다. 아직 돈이 빠져나가지는 않는다.
     *  토스는 이 값으로 프론트가 결제위젯을 띄우고, 카카오페이는 내려준 redirect 주소로 이동시킨다.
     * */
    @PostMapping("/prepare")
    public ResponseEntity<ApiResponse<PreparePaymentResponse>> prepare(
            @RequestBody @Valid PreparePaymentRequest request,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        // 요청 DTO -> 명령 객체로 변환해서 서비스 호출
        PreparePaymentResult result = paymentCommandUsecase.preparePayment(
                request.toCommand(principal.getId()));

        return ResponseEntity.ok(ApiResponse.success(PaymentResponseCode.PAYMENT_PREPARED,
                PaymentResponseMessage.PAYMENT_PREPARED, PreparePaymentResponse.from(result)));
    }

}
