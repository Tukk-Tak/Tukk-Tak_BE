package com.ohgiraffer.tukk_tak.tukk_tak.member.domain.repository;

import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.BusinessVerification;

import java.util.Optional;

/*
 * comment.
 *  사업자 인증 기록 저장소 포트(인터페이스)
 *  서비스는 이 인터페이스만 알고, JPA 같은 구현 기술은 모른다.
 * */
public interface BusinessVerificationRepository {

    // 이미 다른 회원이 인증한 사업자번호인지 (숫자 10자리로 정규화된 값으로 조회)
    boolean existsByBusinessNumber(String businessNumber);

    // 특정 회원이 이미 인증해 둔 사업자번호인지 (탈퇴한 회원이 같은 번호로 복구 가입할 때 중복으로 보지 않기 위함)
    boolean existsByMemberIdAndBusinessNumber(Long memberId, String businessNumber);

    // 회원이 인증을 마친 사업자가 있는지 (APPROVED만 저장하므로 존재 여부로 충분하다)
    boolean existsByMemberId(Long memberId);

    // 인증 기록 저장 (id가 없으면 새로 만들고, 있으면 갱신한다)
    BusinessVerification save(BusinessVerification businessVerification);

    // 회원의 현재 사업자 (가장 최근에 인증한 기록, 인증한 적이 없으면 비어 있다)
    Optional<BusinessVerification> findLatestByMemberId(Long memberId);

    // 회원이 특정 사업자번호로 인증해 둔 기록 (예전 번호로 돌아갈 때 갱신하려고 찾는다)
    Optional<BusinessVerification> findByMemberIdAndBusinessNumber(Long memberId, String businessNumber);

}
