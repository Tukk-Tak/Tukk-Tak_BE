package com.ohgiraffer.tukk_tak.tukk_tak.community.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record CreatePostRequest(


        @NotBlank(message = "제목은 필수입니다.")
        String title,

        @NotBlank(message = "설명은 필수입니다.")
        String content,

        List<String> tags,

        MultipartFile image

) {
}
