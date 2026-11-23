package com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.request;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.command.PreparePaymentCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentMethod;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/*
 * comment.
 *  결제 준비 요청 DTO
 *  금액은 받지 않는다. 서버가 플랜 가격 x 개월 수로 계산한다.
 *  개월 수의 상한은 서비스가 설정값으로 검사한다.
 * */

public record PreparePaymentRequest(
        @NotNull(message = "결제 수단을 선택해주세요.")
        PaymentMethod method,    // TOSS / KAKAOPAY

        @NotNull(message = "플랜을 선택해주세요.")
        PlanName planName,       // BASIC / PREMIUM (FREE는 서비스에서 거절)

        @NotNull(message = "개월 수를 입력해주세요.")
        @Min(value = 1, message = "개월 수는 1 이상이어야 합니다.")
        Integer months           // 구매할 개월 수
) {
    // 요청 DTO -> 명령 객체 (회원 번호는 body가 아니라 토큰에서 꺼낸 값을 쓴다)
    public PreparePaymentCommand toCommand(Long memberId) {
        return new PreparePaymentCommand(memberId, method, planName, months);
    }
}
