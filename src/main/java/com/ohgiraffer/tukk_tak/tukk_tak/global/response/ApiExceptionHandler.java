package com.ohgiraffer.tukk_tak.tukk_tak.global.response;

import com.ohgiraffer.tukk_tak.tukk_tak.global.exception.DomainRuleViolationException;
import com.ohgiraffer.tukk_tak.tukk_tak.global.exception.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/* comment
    개별 도메인에서 잡히지 않는 에러를 잡는 글로벌 에러 핸들러
 */

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    // === 1. 비즈니스 규칙 위반 (400) ===

    @ExceptionHandler(DomainRuleViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDomainRuleViolation(DomainRuleViolationException exception) {
        return build(HttpStatus.BAD_REQUEST, ApiResponseCode.DOMAIN_RULE_VIOLATION, exception.getMessage());
    }

    // === 2. 입력 검증 실패 (400) ===

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        // 검증 오류 표현 방식은 프레젠테이션 규약이므로 여기서 조합한다.
        return build(HttpStatus.BAD_REQUEST, ApiResponseCode.VALIDATION_ERROR, firstFieldMessage(exception));
    }

    // @ModelAttribute, 쿼리 파라미터 바인딩 실패
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiErrorResponse> handleBind(BindException exception) {
        return build(HttpStatus.BAD_REQUEST, ApiResponseCode.VALIDATION_ERROR, firstFieldMessage(exception));
    }

    // @RequestParam, @PathVariable에 붙인 검증 어노테이션 실패
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException exception) {
        return build(HttpStatus.BAD_REQUEST, ApiResponseCode.VALIDATION_ERROR, ApiResponseMessage.VALIDATION_ERROR);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        String message = String.format("'%s' 파라미터 값이 유효하지 않습니다.", exception.getName());
        return build(HttpStatus.BAD_REQUEST, ApiResponseCode.VALIDATION_ERROR, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleNotReadable(HttpMessageNotReadableException exception) {
        return build(HttpStatus.BAD_REQUEST, ApiResponseCode.VALIDATION_ERROR, "요청 본문 형식이 올바르지 않습니다.");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingParam(MissingServletRequestParameterException exception) {
        String message = String.format("필수 파라미터 '%s' 가 누락되었습니다.", exception.getParameterName());
        return build(HttpStatus.BAD_REQUEST, ApiResponseCode.VALIDATION_ERROR, message);
    }

    // === 3. 인증/인가 실패 (401 / 403) ===

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthentication(AuthenticationException exception) {
        return build(HttpStatus.UNAUTHORIZED, ApiResponseCode.UNAUTHORIZED, ApiResponseMessage.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException exception) {
        return build(HttpStatus.FORBIDDEN, ApiResponseCode.FORBIDDEN, ApiResponseMessage.FORBIDDEN);
    }

    // === 4. 리소스 미존재 (404) ===

    // ResourceNotFoundException: 경로는 맞는데 id=999 같은 데이터가 DB에 없을 때, 서비스에서 직접 던지는 예외
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException exception) {
        return build(HttpStatus.NOT_FOUND, ApiResponseCode.NOT_FOUND, exception.getMessage());
    }

    // NoResourceFoundException: 없는 URL로 요청했을 때 Spring이 알아서 던지는 예외 (경로 자체가 없음)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResource(NoResourceFoundException exception) {
        return build(HttpStatus.NOT_FOUND, ApiResponseCode.NOT_FOUND, ApiResponseMessage.NOT_FOUND);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoHandler(NoHandlerFoundException exception) {
        String message = String.format("요청하신 경로 '%s' 를 찾을 수 없습니다.", exception.getRequestURL());
        return build(HttpStatus.NOT_FOUND, ApiResponseCode.NOT_FOUND, message);
    }

    // === 5. 지원하지 않는 요청 (405 / 415) ===

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException exception) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, ApiResponseCode.VALIDATION_ERROR, "지원하지 않는 요청 방식입니다.");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException exception) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ApiResponseCode.VALIDATION_ERROR, "지원하지 않는 요청 형식입니다.");
    }

    // === 6. 예상치 못한 오류 (500, 마지막 안전망) ===

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception) {
        // 마지막 안전망. 내부 예외 메시지는 클라이언트에 노출하지 않고 서버 로그에만 남긴다.
        log.error("[Unexpected] {}", exception.getMessage(), exception);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ApiResponseCode.INTERNAL_ERROR, ApiResponseMessage.INTERNAL_ERROR);
    }

    // === 공통 헬퍼 ===

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status)
                .body(ApiErrorResponse.of(status.value(), code, message));
    }

    private String firstFieldMessage(BindException exception) {
        return exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse(ApiResponseMessage.VALIDATION_ERROR);
    }
}