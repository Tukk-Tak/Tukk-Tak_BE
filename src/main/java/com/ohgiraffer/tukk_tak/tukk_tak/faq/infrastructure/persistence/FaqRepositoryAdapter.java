package com.ohgiraffer.tukk_tak.tukk_tak.faq.infrastructure.persistence;

import com.ohgiraffer.tukk_tak.tukk_tak.faq.domain.model.Faq;
import com.ohgiraffer.tukk_tak.tukk_tak.faq.domain.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FaqRepositoryAdapter implements FaqRepository {

    private final SpringDataFaqRepository springDataFaqRepository;

    @Override
    public Faq findByQuestion(String question) {
        return springDataFaqRepository.findByQuestion(question).toDomain();
    }
}
