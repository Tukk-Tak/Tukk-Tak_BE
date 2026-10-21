package com.ohgiraffer.tukk_tak.tukk_tak.global.response;

import java.time.LocalDateTime;

/*
 * ApiResponse는 성공 응답의 공통 표현 형식이다.
 */
public record ApiResponse<T>(
        LocalDateTime timestamp,
        int status,
        String code,
        String message,
        T data
) {

    public static <T> ApiResponse<T> success(String code, String message, T data) {
        return new ApiResponse<>(LocalDateTime.now(), 200, code, message, data);
    }

    // 생성과 일반 성공을 분리해두면 controller가 REST 의도를 더 명확히 표현할 수 있다.
    public static <T> ApiResponse<T> created(String code, String message, T data) {
        return new ApiResponse<>(LocalDateTime.now(), 201, code, message, data);
    }

    public static ApiResponse<Void> success(String code, String message) {
        return new ApiResponse<>(LocalDateTime.now(), 200, code, message, null);
    }
}
