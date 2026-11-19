package com.ohgiraffer.tukk_tak.tukk_tak.community.application.service.comment;

import com.ohgiraffer.tukk_tak.tukk_tak.community.application.port.GetNicknamePort;
import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.comment.CommentQueryUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.exception.PostNotFoundException;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Post;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.CommentRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.CommentRepository.CommentInfo;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentQueryService implements CommentQueryUsecase {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final GetNicknamePort getNicknamePort;

    @Override
    public CommentListResult getComments(Long postId,Long memberId,int page, int size) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없어요."));

        // 부모 댓글 총 개수로 총 페이지 수 계산
        long totalCount = commentRepository.countComments(postId);
        int totalPages = (int) ((totalCount + size - 1) / size);

        List<CommentInfo> comments = commentRepository.findComments(postId, page, size);
        if (comments.isEmpty()) {
            return new CommentListResult(List.of(), page, size, totalCount, totalPages);
        }

        // 현재 조회하는 사람이 글 작성자인지 판단
        boolean viewerIsPostWriter = memberId != null && memberId.equals(post.getMemberId());

        // 삭제되지 않고 / 공개이거나 비공개일거면 조회자=작성자일 때 -> 조회 가능
        Predicate<CommentInfo> canView = c -> !c.isDeleted()
                && (!c.isPrivate() || viewerIsPostWriter || c.memberId().equals(memberId));

        // 볼 수 있는 댓글의 닉네임 한 번에 조회
        Map<Long, String> nicknames = getNicknamePort.getNicknames(
                comments.stream()
                        .filter(canView)
                        .map(CommentInfo::memberId)
                        .collect(Collectors.toSet()));

        // 대댓글 갯수 세기
        Map<Long, Long> replyCounts = commentRepository.countRepliesByParentIds(
                comments.stream().map(CommentInfo::id).toList());

        List<CommentSearchResult> results = comments.stream()
                .map(c -> {
                    long replyCount = replyCounts.getOrDefault(c.id(), 0L);

                    // 조회 불가능한 댓글들
                    if (!canView.test(c)) {
                        return new CommentSearchResult(
                                c.id(), null, null, null,
                                c.isDeleted() ? replyCount : 0L,
                                c.isPrivate(), c.isDeleted(), false, false);
                    }

                    // 조회 가능한 댓글
                    return new CommentSearchResult(
                            c.id(), nicknames.get(c.memberId()), c.content(), c.createdAt(),
                            replyCount, c.isPrivate(), false,
                            c.memberId().equals(post.getMemberId()),
                            c.memberId().equals(memberId));
                })
                .toList();

        return new CommentListResult(results, page, size, totalCount, totalPages);
    }
}