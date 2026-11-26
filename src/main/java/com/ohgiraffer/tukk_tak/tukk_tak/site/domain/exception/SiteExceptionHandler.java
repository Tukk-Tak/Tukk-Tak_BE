package com.ohgiraffer.tukk_tak.tukk_tak.site.domain.exception;

import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiErrorResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(0)
@RestControllerAdvice
public class SiteExceptionHandler {

    // 존재하지 않는 사이트
    @ExceptionHandler(SiteNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleSiteNotFound(SiteNotFoundException e) {
        return build(HttpStatus.NOT_FOUND, "SITE_NOT_FOUND", e.getMessage());
    }

    // 본인 사이트가 아닌 경우
    @ExceptionHandler(SiteForbiddenException.class)
    public ResponseEntity<ApiErrorResponse> handleSiteForbidden(SiteForbiddenException e) {
        return build(HttpStatus.FORBIDDEN, "SITE_FORBIDDEN", e.getMessage());
    }

    // 플랜의 저장 개수 한도를 넘은 경우
    @ExceptionHandler(SiteLimitExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleSiteLimitExceeded(SiteLimitExceededException e) {
        return build(HttpStatus.FORBIDDEN, "SITE_LIMIT_EXCEEDED", e.getMessage());
    }

    // AI 초안 생성 횟수 한도 초과
    @ExceptionHandler(AiDraftLimitExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleAiDraftLimitExceeded(AiDraftLimitExceededException e) {
        return build(HttpStatus.FORBIDDEN, "AI_DRAFT_LIMIT_EXCEEDED", e.getMessage());
    }

    @ExceptionHandler(MissingSiteImageException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingSiteImage(MissingSiteImageException e) {
        return build(HttpStatus.BAD_REQUEST, "MISSING_SITE_IMAGE", e.getMessage());
    }

    @ExceptionHandler(InvalidSiteImageException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidSiteImage(InvalidSiteImageException e) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_SITE_IMAGE", e.getMessage());
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status)
                .body(ApiErrorResponse.of(status.value(), code, message));
    }
}