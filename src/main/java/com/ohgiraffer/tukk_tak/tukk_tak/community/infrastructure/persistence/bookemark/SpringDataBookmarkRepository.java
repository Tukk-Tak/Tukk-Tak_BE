package com.ohgiraffer.tukk_tak.tukk_tak.community.infrastructure.persistence.bookemark;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataBookmarkRepository extends JpaRepository<BookmarkJpaEntity,Long> {

    boolean existsByPostIdAndMemberId(Long postId, Long memberId);

    @Modifying
    @Query("DELETE FROM BookmarkJpaEntity b WHERE b.postId = :postId AND b.memberId = :memberId")
    void deleteByPostIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);
}
