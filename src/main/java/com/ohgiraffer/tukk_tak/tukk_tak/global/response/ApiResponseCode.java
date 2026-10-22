package com.ohgiraffer.tukk_tak.tukk_tak.global.response;

/*
 * ApiResponseCode는 API 예외처리 계약에서 사용하는 비즈니스 응답 코드 모음이다.
 */
public final class ApiResponseCode {

    private ApiResponseCode() {
    }

    // ===== 공통 성공 =====
    public static final String SUCCESS = "COMMON-SUCCESS";
    public static final String CREATED = "COMMON-CREATED";

    // ===== 공통 실패 =====
    public static final String VALIDATION_ERROR        = "COMMON-VALIDATION-ERROR";
    public static final String DOMAIN_RULE_VIOLATION   = "COMMON-DOMAIN-RULE-VIOLATION";
    public static final String NOT_FOUND               = "COMMON-NOT-FOUND";
    public static final String UNAUTHORIZED            = "COMMON-UNAUTHORIZED";
    public static final String FORBIDDEN               = "COMMON-FORBIDDEN";
    public static final String INTERNAL_ERROR          = "COMMON-INTERNAL-ERROR";
}
