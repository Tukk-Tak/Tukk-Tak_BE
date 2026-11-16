package com.ohgiraffer.tukk_tak.tukk_tak.community.application.command;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record CreatePostCommand(
        Long memberId,
        String title,
        String content,
        List<String> tags,
        MultipartFile image
) {
}
