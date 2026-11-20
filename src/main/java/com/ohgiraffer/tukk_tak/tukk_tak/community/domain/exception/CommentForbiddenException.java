package com.ohgiraffer.tukk_tak.tukk_tak.community.domain.exception;

public class CommentForbiddenException extends RuntimeException {
    public CommentForbiddenException(String message) {
        super(message);
    }
}
