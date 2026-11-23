package com.ohgiraffer.tukk_tak.tukk_tak.payment.presentation.api.common;

import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiErrorResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.exception.PaymentException;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.exception.PaymentFailure;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
 * comment.
 *  payment 컨텍스트 전용 예외 처리
 *  -> global ApiExceptionHandler 건드리지 않고 payment 예외만 독립적으로 처리
 *  -> 응답 형식은 global의 ApiErrorResponse를 그대로 따른다.
 *  -
 *  @Order(HIGHEST_PRECEDENCE): 글로벌 핸들러의 Exception.class(마지막 안전망)보다 먼저 실행되게 한다.
 * */

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PaymentExceptionHandler {

    // 상태코드, 응답 코드, 문구를 한 묶음으로 다루기 위한 내부 구조
    private record Spec(HttpStatus status, String code, String message) {}

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ApiErrorResponse> handlePayment(PaymentException exception) {
        Spec spec = specOf(exception.getFailure());
        return ResponseEntity.status(spec.status())
                .body(ApiErrorResponse.of(spec.status().value(), spec.code(), spec.message()));
    }

    // switch 식이라 PaymentFailure에 값을 추가하고 여기를 안 고치면 컴파일 에러가 난다 (매핑 누락 방지)
    // 403/404는 글로벌 전용이라 여기서는 쓰지 않는다
    private Spec specOf(PaymentFailure failure) {
        return switch (failure) {
            case PLAN_NOT_PURCHASABLE -> new Spec(HttpStatus.BAD_REQUEST,
                    PaymentResponseCode.PLAN_NOT_PURCHASABLE, PaymentResponseMessage.PLAN_NOT_PURCHASABLE);
            case MONTHS_INVALID -> new Spec(HttpStatus.BAD_REQUEST,
                    PaymentResponseCode.MONTHS_INVALID, PaymentResponseMessage.MONTHS_INVALID);
            case GATEWAY_UNAVAILABLE -> new Spec(HttpStatus.SERVICE_UNAVAILABLE,
                    PaymentResponseCode.GATEWAY_UNAVAILABLE, PaymentResponseMessage.GATEWAY_UNAVAILABLE);
        };
    }


}
