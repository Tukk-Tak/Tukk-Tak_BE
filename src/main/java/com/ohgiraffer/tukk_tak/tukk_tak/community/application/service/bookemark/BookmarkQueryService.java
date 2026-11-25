package com.ohgiraffer.tukk_tak.tukk_tak.community.application.service.bookemark;

import com.ohgiraffer.tukk_tak.tukk_tak.community.application.port.GetNicknamePort;
import com.ohgiraffer.tukk_tak.tukk_tak.community.application.usecase.bookemark.BookmarkQueryUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.community.domain.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkQueryService implements BookmarkQueryUsecase {

    private final BookmarkRepository bookmarkRepository;
    private final GetNicknamePort getNicknamePort;

    @Override
    public BookmarkListResult getBookmarkedPosts(Long memberId, int page, int size) {

        // 북마크한 글 총 개수로 총 페이지 수
        long totalCount = bookmarkRepository.countBookmarkedPosts(memberId);
        int totalPages = (int) ((totalCount + size - 1) / size);

        List<BookmarkRepository.BookmarkedPostInfo> posts = bookmarkRepository.findBookmarkedPosts(memberId, page, size);

        Map<Long, String> nicknames = getNicknamePort.getNicknames(
                posts.stream().map(BookmarkRepository.BookmarkedPostInfo::memberId).collect(Collectors.toSet()));

        List<BookmarkedPostSearchResult> results = posts.stream()
                .map(p -> new BookmarkedPostSearchResult(
                        p.id(), p.title(), p.imageUrl(), nicknames.get(p.memberId()),
                        p.likeCount(), p.commentCount(), p.liked()))
                .toList();

        return new BookmarkListResult(results, page, size, totalCount, totalPages);
    }
}
