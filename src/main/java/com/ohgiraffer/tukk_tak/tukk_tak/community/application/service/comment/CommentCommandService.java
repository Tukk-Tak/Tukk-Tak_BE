package com.ohgiraffer.tukk_tak.tukk_tak.community.application.service.comment;

import com.ohgiraffer.tukk_tak.tukk_tak.community.application.command.CreateCommentCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.community.application.event.CommentNotificationPublisher;
import com.ohgiraffer.tukk_tak.tukk_tak.community.application.policy.CommentSearchPolicy;
import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.comment.CommentCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.exception.CommentNotFoundException;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.exception.PostNotFoundException;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Comment;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Post;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.CommentRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.CommentRepository.CommentInfo;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentCommandService implements CommentCommandUsecase {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentNotificationPublisher commentNotificationPublisher;
    private final CommentSearchPolicy commentSearchPolicy;

    @Override
    public void createComment(CreateCommentCommand command) {

        // 없거나 삭제된 글에는 댓글을 달 수 없음
        Post post = postRepository.findById(command.postId())
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없어요."));

        // 대댓글일 경우
        // 1. 같은 글의 부모 댓글인지 확인
        // 2. 삭제된 부모에는 댓글 달 수 X
        // 3. 부모 댓글이 비공개 댓글인 경우 글 작성자/부모 작성자만 가능
        CommentInfo parent = null;
        if (command.parentCommentId() != null) {
            parent = commentRepository.findParentComment(command.postId(), command.parentCommentId())
                    .filter(p -> !p.isDeleted())
                    .filter(p -> commentSearchPolicy.canViewReplies(p, command.memberId(), post.getMemberId()))
                    .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없어요."));
        }

        postRepository.plusCommentCount(command.postId());
        commentRepository.save(Comment.create(command.memberId(), command.postId(),
                command.parentCommentId(), command.content(), command.isPrivate()));

        commentNotificationPublisher.publish(command, post, parent);
    }

    @Override
    public void deleteComment(Long commentId, Long memberId, boolean isAdmin) {

        Comment comment = commentRepository.findById(commentId)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없어요."));

        comment.delete(memberId, isAdmin);

        postRepository.minusCommentCount(comment.getPostId());

        //  동시에 같은 댓글을 지우면 늦은 요청은 삭제 실패 -> 예외
        if (!commentRepository.isDeleted(commentId, comment.getDeletedAt())) {
            throw new CommentNotFoundException("이미 삭제된 댓글이에요.");
        }
    }
}
