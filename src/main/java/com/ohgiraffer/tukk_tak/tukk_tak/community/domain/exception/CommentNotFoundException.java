package com.ohgiraffer.tukk_tak.tukk_tak.community.domain.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(String message) {
        super(message);
    }
}
