package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.command;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentMethod;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;

/*
 * comment.
 *  결제 준비 명령
 *  컨트롤러(요청 DTO)에서 서비스로 넘기는 값 묶음이다. 금액은 받지 않고 서비스가 계산한다.
 * */

public record PreparePaymentCommand(
        Long memberId,           // 로그인한 회원 (토큰에서 꺼낸 값)
        PaymentMethod method,    // 결제 수단
        PlanName planName,       // 구매할 플랜
        int months               // 구매할 개월 수
) {
}
