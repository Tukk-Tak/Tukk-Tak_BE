package com.ohgiraffer.tukk_tak.tukk_tak.member.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/*
 * comment.
 *  Spring Data JPA 인터페이스 (member 테이블 조회/저장)
 * */

public interface MemberJpaRepository extends JpaRepository<MemberJpaEntity, Long> {

    Optional<MemberJpaEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

}
