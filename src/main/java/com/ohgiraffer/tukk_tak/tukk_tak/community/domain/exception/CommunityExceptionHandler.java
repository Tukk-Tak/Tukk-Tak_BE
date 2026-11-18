package com.ohgiraffer.tukk_tak.tukk_tak.community.domain.exception;

import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@Order(0)
@RestControllerAdvice
public class CommunityExceptionHandler {

    // 제목, 내용 글자 수 초과
    @ExceptionHandler(InvalidPostContentException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidPostContent(InvalidPostContentException e) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_POST_CONTENT", e.getMessage());
    }

    // 태그 개수, 글자 수, 허용되지 않는 문자, 중복
    @ExceptionHandler(InvalidTagException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidTag(InvalidTagException e) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_TAG", e.getMessage());
    }

    // 사진 누락
    @ExceptionHandler(MissingImageException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingImage(MissingImageException e) {
        return build(HttpStatus.BAD_REQUEST, "MISSING_IMAGE", e.getMessage());
    }

    // 사진 용량 초과, 허용되지 않는 형식
    @ExceptionHandler(InvalidImageException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidImage(InvalidImageException e) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_IMAGE", e.getMessage());
    }

    // 정책까지 오기 전에 Spring의 multipart 용량 제한(yml)에서 먼저 막힌 경우
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleMaxUploadSize(MaxUploadSizeExceededException e) {
        log.warn("업로드 제한 초과: {}", e.getMessage(), e);
        return build(HttpStatus.PAYLOAD_TOO_LARGE, "INVALID_IMAGE", "사진은 최대 5MB까지 올릴 수 있어요.");
    }

    // 존재하지 않거나 이미 삭제된 게시글
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handlePostNotFound(PostNotFoundException e) {
        return build(HttpStatus.NOT_FOUND, "POST_NOT_FOUND", e.getMessage());
    }

    // 본인이 쓰지 않은 게시글 삭제 시도
    @ExceptionHandler(PostForbiddenException.class)
    public ResponseEntity<ApiErrorResponse> handlePostForbidden(PostForbiddenException e) {
        return build(HttpStatus.FORBIDDEN, "POST_FORBIDDEN", e.getMessage());
    }

    // 동시 요청으로 이미 좋아요가 등록된 경우 (유니크 제약)
    @ExceptionHandler(LikeAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleLikeAlreadyExists(LikeAlreadyExistsException e) {
        return build(HttpStatus.CONFLICT, "LIKE_ALREADY_EXISTS", e.getMessage());
    }

    // 동시 요청으로 이미 북마크가 등록된 경우 (유니크 제약)
    @ExceptionHandler(BookmarkAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleBookmarkAlreadyExists(BookmarkAlreadyExistsException e) {
        return build(HttpStatus.CONFLICT, "BOOKMARK_ALREADY_EXISTS", e.getMessage());
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status)
                .body(ApiErrorResponse.of(status.value(), code, message));
    }
}