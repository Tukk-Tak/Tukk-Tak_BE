package com.ohgiraffer.tukk_tak.tukk_tak.faq.presentation.api;

import com.ohgiraffer.tukk_tak.tukk_tak.faq.application.usecase.FaqUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.faq.presentation.api.response.AnswerFaq;
import com.ohgiraffer.tukk_tak.tukk_tak.faq.presentation.api.response.FaqResponseCode;
import com.ohgiraffer.tukk_tak.tukk_tak.faq.presentation.api.response.FaqResponseMessage;
import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/faq")
public class FaqController {

    private final FaqUsecase faqUsecase;

    @GetMapping("/answer")
    public ResponseEntity<ApiResponse<AnswerFaq>> getFaq(
            @RequestParam @NotBlank String question
            ){

        return ResponseEntity.ok(ApiResponse.success(
                FaqResponseCode.ANSWER_FOUND,
                FaqResponseMessage.ANSWER_FOUND,
                new AnswerFaq(faqUsecase.getFaqAnswer(question))
        ));
    }
}
