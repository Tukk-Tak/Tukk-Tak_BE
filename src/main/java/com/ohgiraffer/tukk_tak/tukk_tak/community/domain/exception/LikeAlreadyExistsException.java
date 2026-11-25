package com.ohgiraffer.tukk_tak.tukk_tak.community.domain.exception;

public class LikeAlreadyExistsException extends RuntimeException {
    public LikeAlreadyExistsException(String message) {
        super(message);
    }
}
