package com.ohgiraffer.tukk_tak.tukk_tak.faq.infrastructure.persistence;

import com.ohgiraffer.tukk_tak.tukk_tak.faq.domain.model.Faq;
import com.ohgiraffer.tukk_tak.tukk_tak.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "faq")
public class FaqJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question", nullable = false)
    private String question;

    @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
    private String answer;

    public Faq toDomain() {
        return new Faq(id, question, answer);
    }
}
