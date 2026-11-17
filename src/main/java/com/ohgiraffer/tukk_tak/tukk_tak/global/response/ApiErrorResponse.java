package com.ohgiraffer.tukk_tak.tukk_tak.global.response;

import java.time.LocalDateTime;

/*
 * ApiErrorResponse는 예외 상황을 외부 클라이언트에 일관된 형태로 전달하기 위한 표준 모델이다.
 */
public record ApiErrorResponse(
        LocalDateTime timestamp,
        int status,
        String code,
        String message
) {

    public static ApiErrorResponse of(int status, String code, String message) {
        return new ApiErrorResponse(LocalDateTime.now(), status, code, message);
    }
}
