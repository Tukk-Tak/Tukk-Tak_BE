package com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.request;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.command.QuotePaymentCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/*
 * comment.
 *  결제 미리보기 요청 (쿼리스트링: ?planName=BASIC&months=2)
 *  최대 개월 수는 설정값이라 서비스에서 검사하고, 여기서는 비었는지와 1 이상인지만 본다.
 * */

public record QuotePaymentRequest(
        @NotNull(message = "플랜을 선택해 주세요.")
        PlanName planName,

        @NotNull(message = "이용 개월 수를 입력해 주세요.")
        @Min(value = 1, message = "이용 개월 수는 1개월 이상이어야 합니다.")
        Integer months
) {
    public QuotePaymentCommand toCommand(Long memberId) {
        return new QuotePaymentCommand(memberId, planName, months);
    }
}
