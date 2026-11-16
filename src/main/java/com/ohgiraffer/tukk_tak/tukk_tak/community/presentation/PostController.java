package com.ohgiraffer.tukk_tak.tukk_tak.community.presentation;

import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.post.PostQueryUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Sort;
import com.ohgiraffer.tukk_tak.tukk_tak.community.presentation.response.CommunityResponseCode;
import com.ohgiraffer.tukk_tak.tukk_tak.community.presentation.response.CommunityResponseMessage;
import com.ohgiraffer.tukk_tak.tukk_tak.community.presentation.response.GetPostList;
import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostQueryUsecase postQueryUsecase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GetPostList>>> getPostList(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "LATEST") Sort sort,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "6") @Min(1) @Max(30)int size
//            @AuthenticationPrincipal    // 인증인가 구현 후 사용
    ){

//        Long userId = (// == null) ? null : //.id();

//        List<GetPostList> result = postQueryUsecase.getPosts(userId, keyword, page, size).stream()
        List<GetPostList> result = postQueryUsecase.getPosts(null, keyword, sort, page, size).stream()
                .map(GetPostList::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(
                CommunityResponseCode.ANSWER_FOUND,
                CommunityResponseMessage.ANSWER_FOUND,
                result
        ));
    }
}
