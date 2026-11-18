package com.ohgiraffer.tukk_tak.tukk_tak.community.application.service.bookemark;

import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.bookemark.BookmarkQueryUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkQueryService implements BookmarkQueryUsecase {

    private final BookmarkRepository bookmarkRepository;

    @Override
    public List<BookmarkedPostSearchResult> getBookmarkedPosts(Long memberId, int page, int size) {
        return bookmarkRepository.findBookmarkedPosts(memberId, page, size).stream()
                .map(p -> new BookmarkedPostSearchResult(
                        p.id(), p.title(), p.imageUrl(), p.nickname(),
                        p.likeCount(), p.commentCount(), p.liked()))
                .toList();
    }
}
