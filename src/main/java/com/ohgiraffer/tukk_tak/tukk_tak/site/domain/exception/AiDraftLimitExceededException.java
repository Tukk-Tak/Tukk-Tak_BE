package com.ohgiraffer.tukk_tak.tukk_tak.site.domain.exception;

public class AiDraftLimitExceededException extends RuntimeException {
    public AiDraftLimitExceededException(String message) {
        super(message);
    }
}
