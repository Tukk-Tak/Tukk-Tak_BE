package com.ohgiraffer.tukk_tak.tukk_tak.member.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/*
 * comment.
 *  Spring Data JPA 인터페이스 (business_verification 테이블 조회/저장)
 * */

public interface BusinessVerificationJpaRepository extends JpaRepository<BusinessVerificationJpaEntity, Long> {

    boolean existsByBusinessNumber(String businessNumber);
    boolean existsByMemberIdAndBusinessNumber(Long memberId, String businessNumber);
    boolean existsByMemberId(Long memberId);
    Optional<BusinessVerificationJpaEntity> findFirstByMemberIdOrderByVerifiedAtDescIdDesc(Long memberId);
    Optional<BusinessVerificationJpaEntity> findByMemberIdAndBusinessNumber(Long memberId, String businessNumber);

}
