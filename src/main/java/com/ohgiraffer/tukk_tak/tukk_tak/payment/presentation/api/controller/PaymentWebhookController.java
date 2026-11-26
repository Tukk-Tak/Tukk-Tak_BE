package com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.controller;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.PaymentWebhookResult;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.usecase.PaymentWebhookUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.request.TossWebhookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * comment.
 *  결제 웹훅 컨트롤러 (/payments/webhook)
 *  사람이 아니라 PG 서버가 호출해서 로그인 정보가 없다. (SecurityConfig에서 이 경로만 공개)
 *  PG는 응답 본문을 읽지 않고 200 여부만 보므로 ApiResponse를 쓰지 않고 상태 코드만 돌려준다.
 * */

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments/webhook")
public class PaymentWebhookController {

    private final PaymentWebhookUsecase paymentWebhookUsecase;

    /*
     * comment.
     *  토스 웹훅 수신
     *  처리했거나 할 일이 없으면 200, 아직 판단할 수 없으면 503으로 돌려줘서 토스가 나중에 다시 보내게 한다.
     *  PG 조회가 실패하는 등 예외가 나도 200이 아니므로 토스가 다시 보낸다.
     * */

    @PostMapping("/toss")
    public ResponseEntity<Void> receiveToss(@RequestBody TossWebhookRequest request) {
        PaymentWebhookResult result = paymentWebhookUsecase.handleTossWebhook(request.toCommand());

        if (result == PaymentWebhookResult.RETRY) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        return ResponseEntity.ok().build();
    }

}
