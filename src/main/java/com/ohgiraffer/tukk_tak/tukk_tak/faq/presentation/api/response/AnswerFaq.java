package com.ohgiraffer.tukk_tak.tukk_tak.faq.presentation.api.response;

import jakarta.validation.constraints.NotBlank;

public record AnswerFaq(
        @NotBlank String answer
) {
}
