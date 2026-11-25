package com.ohgiraffer.tukk_tak.tukk_tak.subscription.presentation.api.common;

import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiErrorResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.exception.PlanException;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.exception.PlanFailure;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
 * comment.
 *  PlanException을 403 응답으로 바꾸는 핸들러
 *  플랜 한도 위반은 모두 "로그인은 했지만 현재 플랜으로는 할 수 없는 일"이라 403으로 통일한다.
 *  전역 핸들러보다 먼저 적용되도록 우선순위를 가장 높게 둔다.
 * */

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PlanExceptionHandler {

    // 상태코드, 응답 코드, 문구를 한 묶음으로 다루기 위한 내부 구조
    private record Spec(HttpStatus status, String code, String message) {}

    @ExceptionHandler(PlanException.class)
    public ResponseEntity<ApiErrorResponse> handlePlan(PlanException exception) {
        Spec spec = specOf(exception.getFailure());
        return ResponseEntity.status(spec.status())
                .body(ApiErrorResponse.of(spec.status().value(), spec.code(), spec.message()));
    }

    // switch 식이라 PlanFailure에 값을 추가하고 여기를 안 고치면 컴파일 에러가 난다 (매핑 누락 방지)
    // 한도/플랜 제한은 결제(플랜 변경)로 풀리는 문제라 402, 선행 조건 미충족은 400이다
    // 403/404는 글로벌 전용이라 여기서는 쓰지 않는다
    private Spec specOf(PlanFailure failure) {
        return switch (failure) {
            case AI_LIMIT_EXCEEDED -> new Spec(HttpStatus.PAYMENT_REQUIRED,
                    PlanResponseCode.AI_LIMIT_EXCEEDED, PlanResponseMessage.AI_LIMIT_EXCEEDED);
            case SAVE_LIMIT_EXCEEDED -> new Spec(HttpStatus.PAYMENT_REQUIRED,
                    PlanResponseCode.SAVE_LIMIT_EXCEEDED, PlanResponseMessage.SAVE_LIMIT_EXCEEDED);
            case PUBLISH_LIMIT_EXCEEDED -> new Spec(HttpStatus.PAYMENT_REQUIRED,
                    PlanResponseCode.PUBLISH_LIMIT_EXCEEDED, PlanResponseMessage.PUBLISH_LIMIT_EXCEEDED);
            case PUBLISH_NOT_ALLOWED -> new Spec(HttpStatus.PAYMENT_REQUIRED,
                    PlanResponseCode.PUBLISH_NOT_ALLOWED, PlanResponseMessage.PUBLISH_NOT_ALLOWED);
            case EDIT_NOT_ALLOWED -> new Spec(HttpStatus.PAYMENT_REQUIRED,
                    PlanResponseCode.EDIT_NOT_ALLOWED, PlanResponseMessage.EDIT_NOT_ALLOWED);
            case BUSINESS_NOT_VERIFIED -> new Spec(HttpStatus.BAD_REQUEST,
                    PlanResponseCode.BUSINESS_NOT_VERIFIED, PlanResponseMessage.BUSINESS_NOT_VERIFIED);
            case AI_CONSENT_REQUIRED -> new Spec(HttpStatus.BAD_REQUEST,
                    PlanResponseCode.AI_CONSENT_REQUIRED, PlanResponseMessage.AI_CONSENT_REQUIRED);
        };
    }

}
