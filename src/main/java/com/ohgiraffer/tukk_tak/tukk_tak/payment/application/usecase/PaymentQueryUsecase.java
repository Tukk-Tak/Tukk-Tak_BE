package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.usecase;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.command.QuotePaymentCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.QuotePaymentResult;

/*
 * comment.
 *  결제 조회 유스케이스 (아무것도 저장하지 않는 조회)
 * */

public interface PaymentQueryUsecase {

    // 결제 전에 금액과 이용 기간을 미리 계산한다
    QuotePaymentResult quotePayment(QuotePaymentCommand command);

}
