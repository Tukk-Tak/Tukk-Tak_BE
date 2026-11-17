package com.ohgiraffer.tukk_tak.tukk_tak.community.domain.exception;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String message) {
        super(message);
    }
}
