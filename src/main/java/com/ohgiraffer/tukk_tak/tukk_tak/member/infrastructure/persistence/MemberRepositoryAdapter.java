package com.ohgiraffer.tukk_tak.tukk_tak.member.infrastructure.persistence;

import com.ohgiraffer.tukk_tak.tukk_tak.global.exception.ResourceNotFoundException;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Member;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/*
 * comment.
 *  MemberRepository 포트의 구현체
 *  도메인(Member)과 JPA 엔티티(MemberJpaEntity)를 서로 변환한다.
 *  수정할 때는 기존 엔티티를 조회해서 변경 감지로 UPDATE 하므로 createdAt이 유지된다.
 * */

@Repository
@RequiredArgsConstructor
public class MemberRepositoryAdapter implements MemberRepository {

    private final MemberJpaRepository jpaRepository;

    @Override
    public Optional<Member> findById(Long id) {
        return jpaRepository.findById(id).map(MemberJpaEntity::toDomain);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(MemberJpaEntity::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) { return jpaRepository.existsByEmail(email); }

    @Override
    @Transactional
    public Member save(Member member) {
        if (member.getId() == null) {
            return jpaRepository.save(MemberJpaEntity.fromDomain(member)).toDomain();
        }
        MemberJpaEntity entity = jpaRepository.findById(member.getId())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 회원입니다."));
        entity.updateFrom(member);   // 더티체킹으로 UPDATE, createdAt 보존
        return entity.toDomain();
    }

}
