package com.ohgiraffer.tukk_tak.tukk_tak.community.infrastructure.persistence.comment;

import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommentRepositoryAdapter implements CommentRepository {

    private final SpringDataCommentRepository springDataCommentRepository;

    @Override
    public List<CommentInfo> findComments(Long postId, int page, int size) {
        return springDataCommentRepository.findComments(postId, PageRequest.of(page - 1, size)).stream()
                .map(p -> new CommentInfo(
                        p.getId(), p.getMemberId(), p.getContent(), p.getCreatedAt(),
                        p.getIsPrivate(), p.getIsDeleted()))
                .toList();
    }

    @Override
    public Map<Long, Long> countRepliesByParentIds(List<Long> parentIds) {
        if (parentIds.isEmpty()) {
            return Map.of();
        }
        return springDataCommentRepository.countRepliesByParentIds(parentIds).stream()
                .collect(Collectors.toMap(
                        SpringDataCommentRepository.ReplyCountProjection::getParentId,
                        SpringDataCommentRepository.ReplyCountProjection::getReplyCount));
    }

    @Override
    public long countComments(Long postId) {
        return springDataCommentRepository.countComments(postId);
    }

    @Override
    public Optional<CommentInfo> findParentComment(Long postId, Long parentCommentId) {
        return springDataCommentRepository.findByIdAndPostIdAndParentIdIsNull(parentCommentId, postId)
                .map(p -> new CommentInfo(
                        p.getId(), p.getMemberId(), p.getContent(), p.getCreatedAt(),
                        p.getIsPrivate(), p.getIsDeleted()));
    }

    @Override
    public List<CommentInfo> findReplies(Long parentId) {
        return springDataCommentRepository.findByParentIdOrderByCreatedAtAscIdAsc(parentId).stream()
                .map(p -> new CommentInfo(
                        p.getId(), p.getMemberId(), p.getContent(), p.getCreatedAt(),
                        p.getIsPrivate(), p.getIsDeleted()))
                .toList();
    }
}
