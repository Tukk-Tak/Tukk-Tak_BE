package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.usecase;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.command.PreparePaymentCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.PreparePaymentResult;

/*
 * comment.
 *  결제 명령 유스케이스 (컨트롤러가 의존하는 인터페이스)
 *  승인, 웹훅 등은 해당 API를 만들 때 이 인터페이스에 추가한다.
 * */

public interface PaymentCommandUsecase {

    // 결제 준비: 금액과 주문번호를 서버가 확정하고 임시 주문을 저장한다 (돈은 아직 움직이지 않는다)
    PreparePaymentResult preparePayment(PreparePaymentCommand command);

}
