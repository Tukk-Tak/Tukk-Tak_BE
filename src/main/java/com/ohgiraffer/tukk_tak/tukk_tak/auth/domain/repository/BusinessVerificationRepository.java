package com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.repository;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.model.BusinessVerification;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * comment.
 *  사업자 인증 기록 저장소
 *  MemberRepository의 구조(도메인 인터페이스와 JPA 구현을 나누는지)에 맞춰 위치를 조정한다.
 * */

public interface BusinessVerificationRepository extends JpaRepository<BusinessVerification, Long> {

    // 이미 다른 회원이 인증한 사업자번호인지 확인한다.
    // 숫자 10자리로 정규화된 값으로 조회해야 하이픈 유무에 상관없이 같은 번호로 잡힌다.
    boolean existsByBusinessNumber(String businessNumber);

}
