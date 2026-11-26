package com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.controller;

import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.user.CustomUserPrincipal;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.ConfirmPaymentResult;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.PreparePaymentResult;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.QuotePaymentResult;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.usecase.PaymentCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.usecase.PaymentConfirmUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.usecase.PaymentQueryUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.common.PaymentResponseCode;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.common.PaymentResponseMessage;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.request.ConfirmPaymentRequest;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.request.PreparePaymentRequest;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.request.QuotePaymentRequest;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.response.ConfirmPaymentResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.response.PreparePaymentResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.response.QuotePaymentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    private final PaymentQueryUsecase paymentQueryUsecase;
    private final PaymentConfirmUsecase paymentConfirmUsecase;

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

    /*
     * comment.
     *  결제 미리보기 API
     *  플랜과 개월 수로 결제 금액과 이용 기간을 미리 계산해서 보여준다. (아무것도 저장하지 않는다)
     * */

    @GetMapping("/quote")
    public ResponseEntity<ApiResponse<QuotePaymentResponse>> quotePayment(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid QuotePaymentRequest request) {
        QuotePaymentResult result = paymentQueryUsecase.quotePayment(request.toCommand(principal.getId()));

        return ResponseEntity.ok(ApiResponse.success(PaymentResponseCode.PAYMENT_QUOTED,
                PaymentResponseMessage.PAYMENT_QUOTED, QuotePaymentResponse.from(result)));
    }

    /*
     * comment.
     *  결제 승인 API
     *  결제창에서 돌아온 값으로 PG 승인을 요청하고, 성공하면 구독과 결제 내역을 반영한다.
     * */

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<ConfirmPaymentResponse>> confirmPayment(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody ConfirmPaymentRequest request) {
        ConfirmPaymentResult result = paymentConfirmUsecase.confirmPayment(request.toCommand(principal.getId()));

        return ResponseEntity.ok(ApiResponse.success(PaymentResponseCode.PAYMENT_CONFIRMED,
                PaymentResponseMessage.PAYMENT_CONFIRMED, ConfirmPaymentResponse.from(result)));
    }

}
