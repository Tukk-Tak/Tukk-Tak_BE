package com.ohgiraffer.tukk_tak.tukk_tak.community.presentation;

import com.ohgiraffer.tukk_tak.tukk_tak.community.application.command.CreatePostCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.post.PostCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.post.PostQueryUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Sort;
import com.ohgiraffer.tukk_tak.tukk_tak.community.presentation.request.CreatePostRequest;
import com.ohgiraffer.tukk_tak.tukk_tak.community.presentation.response.CommunityResponseCode;
import com.ohgiraffer.tukk_tak.tukk_tak.community.presentation.response.CommunityResponseMessage;
import com.ohgiraffer.tukk_tak.tukk_tak.community.presentation.response.GetPostList;
import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

    private final PostQueryUsecase postQueryUsecase;
    private final PostCommandUsecase postCommandUsecase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GetPostList>>> getPostList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Sort sort,
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

    // 나중에 사업자 role만 글 쓸 수 있게 수정
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createPost(
            @Valid @ModelAttribute CreatePostRequest request
//            @AuthenticationPrincipal CustomUserDetails userDetails
    ){

        postCommandUsecase.createPost(new CreatePostCommand(
//                userDetails.getUserId(),
                1001L,request.title(),request.content(),request.tags(),request.image()
        ));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                    CommunityResponseCode.POST_CREATED,
                    CommunityResponseMessage.POST_CREATED,
                    null
                ));
    }
}
