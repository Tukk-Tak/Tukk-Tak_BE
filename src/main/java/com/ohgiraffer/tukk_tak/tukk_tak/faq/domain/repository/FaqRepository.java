package com.ohgiraffer.tukk_tak.tukk_tak.faq.domain.repository;

import com.ohgiraffer.tukk_tak.tukk_tak.faq.domain.model.Faq;

public interface FaqRepository {
    Faq findByQuestion(String question);
}
