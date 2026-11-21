package com.ohgiraffer.tukk_tak.tukk_tak.auth.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/*
 * comment.
 *  Spring Data JPA 인터페이스 (business_verification 테이블 조회/저장)
 * */

public interface BusinessVerificationJpaRepository extends JpaRepository<BusinessVerificationJpaEntity, Long> {

    boolean existsByBusinessNumber(String businessNumber);

}
