package com.ohgiraffer.tukk_tak.tukk_tak.faq.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.faq.application.usecase.FaqUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.faq.domain.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FaqService implements FaqUsecase {

    private final FaqRepository faqRepository;

    @Override
    public String getFaqAnswer(String question) {
        // faq를 버튼으로 클릭하니까 존재하지 않는 질문이 올 수가 없어서 예외처리X
        return faqRepository.findByQuestion(question).getAnswer();
    }
}
