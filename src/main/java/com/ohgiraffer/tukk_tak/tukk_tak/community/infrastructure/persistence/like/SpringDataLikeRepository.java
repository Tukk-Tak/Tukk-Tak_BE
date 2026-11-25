package com.ohgiraffer.tukk_tak.tukk_tak.community.infrastructure.persistence.like;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataLikeRepository extends JpaRepository<PostLikeJpaEntity, Long> {

    boolean existsByPostIdAndMemberId(Long postId, Long memberId);

    @Modifying
    @Query("DELETE FROM PostLikeJpaEntity l WHERE l.postId = :postId AND l.memberId = :memberId")
    int deleteByPostIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);
}