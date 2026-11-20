package com.ohgiraffer.tukk_tak.tukk_tak.community.presentation;

import com.ohgiraffer.tukk_tak.tukk_tak.community.application.command.CreatePostCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.bookemark.BookmarkCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.bookemark.BookmarkQueryUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.comment.CommentQueryUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.like.LikeCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.post.PostCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.post.PostQueryUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Sort;
import com.ohgiraffer.tukk_tak.tukk_tak.community.presentation.request.CreatePostRequest;
import com.ohgiraffer.tukk_tak.tukk_tak.community.presentation.response.*;
import com.ohgiraffer.tukk_tak.tukk_tak.global.response.ApiResponse;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.user.CustomUserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class CommunityController {

    private final PostQueryUsecase postQueryUsecase;
    private final PostCommandUsecase postCommandUsecase;
    private final LikeCommandUsecase likeCommandUsecase;
    private final BookmarkCommandUsecase bookmarkCommandUsecase;
    private final BookmarkQueryUsecase bookmarkQueryUsecase;
    private final CommentQueryUsecase commentQueryUsecase;

    @GetMapping
    public ResponseEntity<ApiResponse<GetPostList>> getPostList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Sort sort,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "6") @Min(1) @Max(30)int size
            , @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal
    ){
        Long userId = (customUserPrincipal == null) ? null : customUserPrincipal.getId();

        GetPostList result = GetPostList.from(
                postQueryUsecase.getPosts(userId, keyword, sort, page, size));

        return ResponseEntity.ok(ApiResponse.success(
                CommunityResponseCode.ANSWER_FOUND,
                CommunityResponseMessage.ANSWER_FOUND,
                result
        ));
    }


    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createPost(
            @Valid @ModelAttribute CreatePostRequest request
            , @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal
    ){

        Long postId = postCommandUsecase.createPost(new CreatePostCommand(
                customUserPrincipal.getId(), request.title(),request.content(),request.tags(),request.image()
        ));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                    CommunityResponseCode.CREATE_SUCCESS,
                    CommunityResponseMessage.CREATE_SUCCESS,
                        postId
                ));
    }


    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId
            , @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal
    ){
        postCommandUsecase.deletePost(postId,customUserPrincipal.getId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        CommunityResponseCode.DELETE_SUCCESS,
                        CommunityResponseMessage.DELETE_SUCCESS
                ));
    }

    // 나중에 사업자 role만 글 쓸 수 있게 수정
    @PostMapping("/like/{postId}")
    public ResponseEntity<ApiResponse<Boolean>> like(
            @PathVariable Long postId
            , @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal
    ){
        boolean liked= likeCommandUsecase.like(postId,customUserPrincipal.getId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        CommunityResponseCode.LIKE_SUCCESS,
                        CommunityResponseMessage.LIKE_SUCCESS,
                        liked
                ));
    }

    // 나중에 사업자 role만 글 쓸 수 있게 수정
    @PostMapping("/bookmark/{postId}")
    public ResponseEntity<ApiResponse<Boolean>> bookmark(
            @PathVariable Long postId
            , @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal
    ){
        boolean bookmarked = bookmarkCommandUsecase.bookmark(postId,customUserPrincipal.getId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        CommunityResponseCode.BOOKMARK_SUCCESS,
                        CommunityResponseMessage.BOOKMARK_SUCCESS,
                        bookmarked
                ));
    }


    // 나중에 사업자 role만 글 쓸 수 있게 수정
    @GetMapping("/bookmarklist")
    public ResponseEntity<ApiResponse<GetBookmarkList>> getBookmarkList(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "6") @Min(1) @Max(30)int size
            , @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal
    ){

        GetBookmarkList result = GetBookmarkList.from(
                bookmarkQueryUsecase.getBookmarkedPosts(customUserPrincipal.getId(), page, size));

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        CommunityResponseCode.ANSWER_FOUND,
                        CommunityResponseMessage.ANSWER_FOUND,
                        result
                ));
    }


    @GetMapping("/detail/{postId}")
    public ResponseEntity<ApiResponse<GetPostDetail>> getPostDetail(
            @PathVariable Long postId
        , @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal
    ){
                Long userId = (customUserPrincipal == null) ? null : customUserPrincipal.getId();

        GetPostDetail result = GetPostDetail.from(postQueryUsecase.getPostDetail(userId, postId));

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        CommunityResponseCode.ANSWER_FOUND,
                        CommunityResponseMessage.ANSWER_FOUND,
                        result
                ));
    }

    @GetMapping("/comment/{postId}")
    public ResponseEntity<ApiResponse<GetComments>> getComments(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(30)int size
    ){

        Long userId = (customUserPrincipal == null) ? null : customUserPrincipal.getId();

        GetComments result = GetComments.from(
                commentQueryUsecase.getComments(postId, userId, page, size));

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        CommunityResponseCode.ANSWER_FOUND,
                        CommunityResponseMessage.ANSWER_FOUND,
                        result
                ));
    }


    @GetMapping("/comment/{postId}/{parentCommentId}")
    public ResponseEntity<ApiResponse<List<GetReplyComments>>> getReplyComments(
            @PathVariable Long postId,
            @PathVariable Long parentCommentId,
            @AuthenticationPrincipal CustomUserPrincipal customUserPrincipal
    ){
        Long userId = (customUserPrincipal == null) ? null : customUserPrincipal.getId();

        List<GetReplyComments> result = commentQueryUsecase.getReplyComments(postId,parentCommentId,userId).stream()
                .map(GetReplyComments::from)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        CommunityResponseCode.ANSWER_FOUND,
                        CommunityResponseMessage.ANSWER_FOUND,
                        result
                ));
    }
}
