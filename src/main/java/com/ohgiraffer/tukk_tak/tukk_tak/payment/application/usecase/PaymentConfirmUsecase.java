package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.usecase;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.command.ConfirmPaymentCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.ConfirmPaymentResult;

/*
 * comment.
 *  결제 승인 유스케이스
 * */

public interface PaymentConfirmUsecase {

    // PG 승인을 요청하고, 성공하면 구독과 결제 내역을 반영한다
    ConfirmPaymentResult confirmPayment(ConfirmPaymentCommand command);

}
