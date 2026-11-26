package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentMethod;

import java.time.LocalDate;

/*
 * comment.
 *  결제 준비 결과
 *  startDate, endDate는 지금 기준의 "예상" 기간이고, 실제 기간은 승인 때 다시 계산한다.
 *  redirect 주소는 카카오페이만 값이 있고, 토스는 프론트가 결제위젯으로 결제하므로 null이다.
 * */

public record PreparePaymentResult(
        PaymentMethod method,
        String orderId,
        String orderName,
        int amount,
        LocalDate startDate,
        LocalDate endDate,
        String redirectPcUrl,
        String redirectMobileUrl
) {
    // 토스: 주문 정보만 내려주고, 결제창은 프론트가 띄운다
    public static PreparePaymentResult toss(String orderId, String orderName, int amount,
                                            LocalDate startDate, LocalDate endDate) {
        return new PreparePaymentResult(PaymentMethod.TOSS, orderId, orderName, amount,
                startDate, endDate, null, null);
    }

    // 카카오페이: ready 호출로 받은 이동 주소를 같이 내려준다
    public static PreparePaymentResult kakao(String orderId, String orderName, int amount,
                                             LocalDate startDate, LocalDate endDate,
                                             String redirectPcUrl, String redirectMobileUrl) {
        return new PreparePaymentResult(PaymentMethod.KAKAOPAY, orderId, orderName, amount,
                startDate, endDate, redirectPcUrl, redirectMobileUrl);
    }
}
