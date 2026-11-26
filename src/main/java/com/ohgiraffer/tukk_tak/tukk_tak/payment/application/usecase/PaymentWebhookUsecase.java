package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.usecase;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.command.TossWebhookCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.PaymentWebhookResult;

/*
 * comment.
 *  결제 웹훅 유스케이스
 * */

public interface PaymentWebhookUsecase {

    // 토스 웹훅을 처리한다 (결제 상태 변경 이벤트만 다룬다)
    PaymentWebhookResult handleTossWebhook(TossWebhookCommand command);

}
