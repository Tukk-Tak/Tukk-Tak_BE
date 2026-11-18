package com.ohgiraffer.tukk_tak.tukk_tak.community.application.service.bookemark;

import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.bookemark.BookmarkCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.exception.PostNotFoundException;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.model.Bookmark;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.BookmarkRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkCommandService implements BookmarkCommandUsecase {

    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;

    @Override
    public boolean bookmark(Long postId, Long memberId) {

        postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없어요."));

        if (bookmarkRepository.exists(postId, memberId)) {
            if (bookmarkRepository.delete(postId, memberId) == 1) {
                return false;
            }
        }

        bookmarkRepository.save(Bookmark.create(postId, memberId));
        return true;
    }
}
