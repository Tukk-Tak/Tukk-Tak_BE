package com.ohgiraffer.tukk_tak.tukk_tak.member.domain.repository;

import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Member;

import java.util.Optional;

/*
 * comment.
 *  회원 저장소 포트(인터페이스)
 *  서비스는 이 인터페이스만 알고, JPA 같은 구현 기술은 모른다.
 * */

public interface MemberRepository {

    Optional<Member> findById(Long id);
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    Member save(Member member);

}
