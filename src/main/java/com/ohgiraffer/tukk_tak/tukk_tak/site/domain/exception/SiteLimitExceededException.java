package com.ohgiraffer.tukk_tak.tukk_tak.site.domain.exception;

public class SiteLimitExceededException extends RuntimeException {
    public SiteLimitExceededException(String message) {
        super(message);
    }
}
