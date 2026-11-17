package com.ohgiraffer.tukk_tak.tukk_tak.faq.domain.model;

import lombok.Getter;

@Getter
public class Faq {

    private final Long id;
    private final String question;
    private final String answer;

    public Faq(Long id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
    }
}
