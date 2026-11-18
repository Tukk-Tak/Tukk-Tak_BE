package com.ohgiraffer.tukk_tak.tukk_tak.community.domain.exception;

public class BookmarkAlreadyExistsException extends RuntimeException {
    public BookmarkAlreadyExistsException(String message) {
        super(message);
    }
}
