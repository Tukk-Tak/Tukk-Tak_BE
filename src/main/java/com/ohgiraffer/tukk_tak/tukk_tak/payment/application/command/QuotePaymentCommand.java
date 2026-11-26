package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.command;

import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;

/*
 * comment.
 *  결제 미리보기 요청 값 (플랜과 개월 수)
 *  회원은 로그인 정보에서 꺼내고, 날짜 계산은 서비스가 한다.
 * */

public record QuotePaymentCommand(
        Long memberId,
        PlanName planName,
        int months              // 이용 개월 수
) {
}
