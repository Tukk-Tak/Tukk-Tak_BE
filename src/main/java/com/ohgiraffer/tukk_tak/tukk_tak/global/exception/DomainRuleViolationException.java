package com.ohgiraffer.tukk_tak.tukk_tak.global.exception;

/*
 * DomainRuleViolationException은 도메인 규칙 위반을 표현하는 공통 예외다.
 */
public class DomainRuleViolationException extends RuntimeException {

    public DomainRuleViolationException(String message) {
        super(message);
    }

    public DomainRuleViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
