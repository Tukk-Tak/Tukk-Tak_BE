package com.ohgiraffer.tukk_tak.tukk_tak.community.presentation;

import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.post.PostQueryUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.community.presentation.response.CommunityResponseCode;
import com.ohgiraffer.tukk_tak.tukk_tak.community.presentation.response.CommunityResponseMessage;
import com.ohgiraffer.tukk_tak.tukk_tak.community.presentation.response.GetPostList;
import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiResponse;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostQueryUsecase postQueryUsecase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GetPostList>>> getPostList(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "6") @Min(1) int size
//            @AuthenticationPrincipal    // 비로그인이면 null
    ){

//        Long userId = (// == null) ? null : //.id();

        List<GetPostList> result = postQueryUsecase.getPosts(keyword, page, size).stream()
                .map(GetPostList::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(
                CommunityResponseCode.ANSWER_FOUND,
                CommunityResponseMessage.ANSWER_FOUND,
                result
        ));
    }
}
