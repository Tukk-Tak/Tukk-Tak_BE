package com.ohgiraffer.tukk_tak.tukk_tak.notification.domain.exception;
import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiErrorResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(0)
@RestControllerAdvice
public class NotificationExceptionHandler {


    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NotificationNotFoundException e) {
        return build(HttpStatus.NOT_FOUND, "NOTIFICATION_NOT_FOUND", e.getMessage());
    }

    @ExceptionHandler(NotificationForbiddenException.class)
    public ResponseEntity<ApiErrorResponse> handleForbidden(NotificationForbiddenException e) {
        return build(HttpStatus.FORBIDDEN, "NOTIFICATION_FORBIDDEN", e.getMessage());
    }

    @ExceptionHandler(InvalidNotificationException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalid(InvalidNotificationException e) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_NOTIFICATION", e.getMessage());
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(ApiErrorResponse.of(status.value(), code, message));
    }
}