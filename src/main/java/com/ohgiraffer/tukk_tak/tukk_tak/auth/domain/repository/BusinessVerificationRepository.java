package com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.repository;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.model.BusinessVerification;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * comment.
 *  사업자 인증 기록 저장소 포트(인터페이스)
 *  서비스는 이 인터페이스만 알고, JPA 같은 구현 기술은 모른다.
 * */
public interface BusinessVerificationRepository {

    // 이미 다른 회원이 인증한 사업자번호인지 (숫자 10자리로 정규화된 값으로 조회)
    boolean existsByBusinessNumber(String businessNumber);

    // 인증 기록 저장 (지금은 새로 만드는 저장만 지원)
    BusinessVerification save(BusinessVerification businessVerification);

}
