package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.common;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.exception.AuthException;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.exception.AuthFailure;
import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiErrorResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
 * comment.
 *  auth 컨텍스트 전용 예외 처리
 *  -> global ApiExceptionHandler 건드리지 않고 auth 예외만 독립적으로 처리
 *  -> 응답 형식은 global의 ApiErrorResponse를 그대로 따른다.
 *  -
 *  @Order(HIGHEST_PRECEDENCE): 글로벌 핸들러의 Exception.class(마지막 안전망)보다 먼저 이 핸들러가 실행되게 한다.
 *  두 핸들러의 우선순위가 같으면 어느 쪽이 먼저 등록되느냐에 따라 결과가 달라지고,
 *  글로벌이 먼저면 auth 예외가 전부 500으로 응답된다.
 * */

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthExceptionHandler {

    // 상태코드, 응답 코드, 문구를 한 묶음으로 다루기 위한 내부 구조
    private record Spec(HttpStatus status, String code, String message) {}

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiErrorResponse> handleAuth(AuthException exception) {
        Spec spec = specOf(exception.getFailure());
        return ResponseEntity.status(spec.status())
                .body(ApiErrorResponse.of(spec.status().value(), spec.code(), spec.message()));
    }

    // switch 식이라 AuthFailure에 값을 추가하고 여기를 안 고치면 컴파일 에러가 난다 (매핑 누락 방지)
    // 403/404는 글로벌 전용이라 여기서는 쓰지 않는다
    private Spec specOf(AuthFailure failure) {
        return switch (failure) {
            case LOGIN_FAILED -> new Spec(HttpStatus.UNAUTHORIZED,
                    AuthResponseCode.LOGIN_FAILED, AuthResponseMessage.LOGIN_FAILED);
            case SOCIAL_ACCOUNT_NO_PASSWORD -> new Spec(HttpStatus.UNAUTHORIZED,
                    AuthResponseCode.SOCIAL_ACCOUNT_NO_PASSWORD, AuthResponseMessage.SOCIAL_ACCOUNT_NO_PASSWORD);
            case WITHDRAWN_MEMBER -> new Spec(HttpStatus.UNAUTHORIZED,
                    AuthResponseCode.WITHDRAWN_MEMBER, AuthResponseMessage.WITHDRAWN_MEMBER);
            case ACCOUNT_LOCKED -> new Spec(HttpStatus.TOO_MANY_REQUESTS,   // 요청 과다 (429)
                    AuthResponseCode.ACCOUNT_LOCKED, AuthResponseMessage.ACCOUNT_LOCKED);
            case MISSING_ACCESS_TOKEN -> new Spec(HttpStatus.UNAUTHORIZED,
                    AuthResponseCode.MISSING_ACCESS_TOKEN, AuthResponseMessage.MISSING_ACCESS_TOKEN);
            case INVALID_TOKEN -> new Spec(HttpStatus.UNAUTHORIZED,
                    AuthResponseCode.INVALID_TOKEN, AuthResponseMessage.INVALID_TOKEN);
            case MISSING_REFRESH_TOKEN -> new Spec(HttpStatus.UNAUTHORIZED,
                    AuthResponseCode.MISSING_REFRESH_TOKEN, AuthResponseMessage.MISSING_REFRESH_TOKEN);
            case INVALID_REFRESH_TOKEN -> new Spec(HttpStatus.UNAUTHORIZED,
                    AuthResponseCode.INVALID_REFRESH_TOKEN, AuthResponseMessage.INVALID_REFRESH_TOKEN);
            case EMAIL_ALREADY_REGISTERED -> new Spec(HttpStatus.CONFLICT,
                    AuthResponseCode.EMAIL_ALREADY_REGISTERED, AuthResponseMessage.EMAIL_ALREADY_REGISTERED);
            case EMAIL_SEND_TOO_FAST -> new Spec(HttpStatus.TOO_MANY_REQUESTS,
                    AuthResponseCode.EMAIL_SEND_TOO_FAST, AuthResponseMessage.EMAIL_SEND_TOO_FAST);
            case EMAIL_SEND_LIMIT_EXCEEDED -> new Spec(HttpStatus.TOO_MANY_REQUESTS,
                    AuthResponseCode.EMAIL_SEND_LIMIT_EXCEEDED, AuthResponseMessage.EMAIL_SEND_LIMIT_EXCEEDED);
            case EMAIL_SEND_FAILED -> new Spec(HttpStatus.SERVICE_UNAVAILABLE,   // 메일 서버 장애 (503)
                    AuthResponseCode.EMAIL_SEND_FAILED, AuthResponseMessage.EMAIL_SEND_FAILED);
            case EMAIL_CODE_EXPIRED -> new Spec(HttpStatus.BAD_REQUEST,
                    AuthResponseCode.EMAIL_CODE_EXPIRED, AuthResponseMessage.EMAIL_CODE_EXPIRED);
            case EMAIL_CODE_MISMATCH -> new Spec(HttpStatus.BAD_REQUEST,
                    AuthResponseCode.EMAIL_CODE_MISMATCH, AuthResponseMessage.EMAIL_CODE_MISMATCH);
            case EMAIL_VERIFY_LOCKED -> new Spec(HttpStatus.TOO_MANY_REQUESTS,
                    AuthResponseCode.EMAIL_VERIFY_LOCKED, AuthResponseMessage.EMAIL_VERIFY_LOCKED);
            case NICKNAME_INVALID -> new Spec(HttpStatus.BAD_REQUEST,
                    AuthResponseCode.NICKNAME_INVALID, AuthResponseMessage.NICKNAME_INVALID);
            case NICKNAME_CHECK_LIMIT_EXCEEDED -> new Spec(HttpStatus.TOO_MANY_REQUESTS,
                    AuthResponseCode.NICKNAME_CHECK_LIMIT_EXCEEDED, AuthResponseMessage.NICKNAME_CHECK_LIMIT_EXCEEDED);
            case EMAIL_TOKEN_INVALID -> new Spec(HttpStatus.BAD_REQUEST,
                    AuthResponseCode.EMAIL_TOKEN_INVALID, AuthResponseMessage.EMAIL_TOKEN_INVALID);
            case NICKNAME_ALREADY_USED -> new Spec(HttpStatus.CONFLICT,
                    AuthResponseCode.NICKNAME_ALREADY_USED, AuthResponseMessage.NICKNAME_ALREADY_USED);
            case GOOGLE_TOKEN_INVALID -> new Spec(HttpStatus.UNAUTHORIZED,
                    AuthResponseCode.GOOGLE_TOKEN_INVALID, AuthResponseMessage.GOOGLE_TOKEN_INVALID);
            case GOOGLE_EMAIL_UNVERIFIED -> new Spec(HttpStatus.BAD_REQUEST,
                    AuthResponseCode.GOOGLE_EMAIL_UNVERIFIED, AuthResponseMessage.GOOGLE_EMAIL_UNVERIFIED);
            case LOCAL_ACCOUNT_EXISTS -> new Spec(HttpStatus.CONFLICT,
                    AuthResponseCode.LOCAL_ACCOUNT_EXISTS, AuthResponseMessage.LOCAL_ACCOUNT_EXISTS);
            case GOOGLE_TOO_MANY_ATTEMPTS -> new Spec(HttpStatus.TOO_MANY_REQUESTS,
                    AuthResponseCode.GOOGLE_TOO_MANY_ATTEMPTS, AuthResponseMessage.GOOGLE_TOO_MANY_ATTEMPTS);
            case GOOGLE_TICKET_INVALID -> new Spec(HttpStatus.BAD_REQUEST,
                    AuthResponseCode.GOOGLE_TICKET_INVALID, AuthResponseMessage.GOOGLE_TICKET_INVALID);
            case EMAIL_NOT_REGISTERED -> new Spec(HttpStatus.BAD_REQUEST,
                    AuthResponseCode.EMAIL_NOT_REGISTERED, AuthResponseMessage.EMAIL_NOT_REGISTERED);
            case PASSWORD_RESET_SOCIAL_ACCOUNT -> new Spec(HttpStatus.BAD_REQUEST,
                    AuthResponseCode.PASSWORD_RESET_SOCIAL_ACCOUNT, AuthResponseMessage.PASSWORD_RESET_SOCIAL_ACCOUNT);
            case PASSWORD_RESET_WITHDRAWN -> new Spec(HttpStatus.BAD_REQUEST,
                    AuthResponseCode.PASSWORD_RESET_WITHDRAWN, AuthResponseMessage.PASSWORD_RESET_WITHDRAWN);
            case PASSWORD_RESET_TOKEN_INVALID -> new Spec(HttpStatus.BAD_REQUEST,
                    AuthResponseCode.PASSWORD_RESET_TOKEN_INVALID, AuthResponseMessage.PASSWORD_RESET_TOKEN_INVALID);
            case PASSWORD_SAME_AS_CURRENT -> new Spec(HttpStatus.BAD_REQUEST,
                    AuthResponseCode.PASSWORD_SAME_AS_CURRENT, AuthResponseMessage.PASSWORD_SAME_AS_CURRENT);
            case CURRENT_PASSWORD_MISMATCH -> new Spec(HttpStatus.BAD_REQUEST,
                    AuthResponseCode.CURRENT_PASSWORD_MISMATCH, AuthResponseMessage.CURRENT_PASSWORD_MISMATCH);
            case PASSWORD_CHANGE_LOCKED -> new Spec(HttpStatus.TOO_MANY_REQUESTS,
                    AuthResponseCode.PASSWORD_CHANGE_LOCKED, AuthResponseMessage.PASSWORD_CHANGE_LOCKED);
            case PASSWORD_CHANGE_SOCIAL_ACCOUNT -> new Spec(HttpStatus.BAD_REQUEST,
                    AuthResponseCode.PASSWORD_CHANGE_SOCIAL_ACCOUNT, AuthResponseMessage.PASSWORD_CHANGE_SOCIAL_ACCOUNT);
        };

    }

}
