package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.port;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.KakaoReadyResult;

/*
 * comment.
 *  PG(결제대행사) 호출 포트(인터페이스)
 *  토스 승인, 카카오 승인, 보상 취소 등은 해당 API를 만들 때 이 인터페이스에 추가한다.
 *  PG를 포트원 등으로 바꿔도 서비스는 건드리지 않고 어댑터만 바꾸면 된다.
 * */

public interface PaymentGatewayPort {

    // 카카오페이 결제 준비(ready): tid와 결제 페이지 이동 주소를 받는다 (실패하면 PaymentException)
    KakaoReadyResult readyKakao(String orderId, Long memberId, String orderName, int amount);

}
