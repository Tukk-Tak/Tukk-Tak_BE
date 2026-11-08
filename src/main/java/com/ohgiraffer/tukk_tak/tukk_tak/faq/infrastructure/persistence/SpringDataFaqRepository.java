package com.ohgiraffer.tukk_tak.tukk_tak.faq.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataFaqRepository extends JpaRepository<FaqJpaEntity,Long> {
    FaqJpaEntity findByQuestion(String question);
}
