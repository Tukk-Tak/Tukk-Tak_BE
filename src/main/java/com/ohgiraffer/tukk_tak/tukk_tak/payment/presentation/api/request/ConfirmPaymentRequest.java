package com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.command.ConfirmPaymentCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentMethod;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/*
 * comment.
 *  결제 승인 요청
 *  결제수단마다 필요한 값이 달라서(토스: paymentKey+amount, 카카오: pgToken) 아래 두 검증으로 나눠서 본다.
 *  금액은 서버가 저장한 주문 금액과 대조하는 용도이고, 결제 금액의 기준은 항상 서버다.
 * */

public record ConfirmPaymentRequest(
        @NotNull(message = "결제수단을 선택해 주세요.")
        PaymentMethod method,

        @NotBlank(message = "주문번호가 필요합니다.")
        String orderId,

        String paymentKey,              // 토스가 결제 완료 화면 주소로 준 paymentKey (토스일 때만)
        Integer amount,                 // 토스가 결제 완료 화면 주소로 준 금액 (토스일 때만)
        String pgToken                  // 카카오가 결제 완료 화면 주소로 준 pg_token (카카오일 때만)
) {
    @JsonIgnore
    @AssertTrue(message = "토스 결제는 paymentKey와 amount가 필요합니다.")
    public boolean isTossFieldsValid() {
        if (method != PaymentMethod.TOSS) {
            return true;
        }
        return paymentKey != null && !paymentKey.isBlank() && amount != null && amount > 0;
    }

    @JsonIgnore
    @AssertTrue(message = "카카오페이 결제는 pgToken이 필요합니다.")
    public boolean isKakaoFieldsValid() {
        if (method != PaymentMethod.KAKAOPAY) {
            return true;
        }
        return pgToken != null && !pgToken.isBlank();
    }

    public ConfirmPaymentCommand toCommand(Long memberId) {
        return new ConfirmPaymentCommand(memberId, method, orderId, paymentKey, amount, pgToken);
    }
}
