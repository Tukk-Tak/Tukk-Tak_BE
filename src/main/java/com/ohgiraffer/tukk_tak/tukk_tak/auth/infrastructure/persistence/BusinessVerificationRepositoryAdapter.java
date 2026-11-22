package com.ohgiraffer.tukk_tak.tukk_tak.auth.infrastructure.persistence;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.model.BusinessVerification;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.repository.BusinessVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/*
 * comment.
 *  BusinessVerificationRepository 포트의 구현체
 *  도메인(BusinessVerification)과 JPA 엔티티를 서로 변환한다.
 *  트랜잭션은 호출하는 SignupRegistrar가 시작하므로 여기서는 붙이지 않는다.
 * */

@Repository
@RequiredArgsConstructor
public class BusinessVerificationRepositoryAdapter implements BusinessVerificationRepository {

    private final BusinessVerificationJpaRepository jpaRepository;

    @Override
    public boolean existsByBusinessNumber(String businessNumber) {
        return jpaRepository.existsByBusinessNumber(businessNumber);
    }

    @Override
    public boolean existsByMemberIdAndBusinessNumber(Long memberId, String businessNumber) {
        return jpaRepository.existsByMemberIdAndBusinessNumber(memberId, businessNumber);
    }

    @Override
    public boolean existsByMemberId(Long memberId) {
        return jpaRepository.existsByMemberId(memberId);
    }

    @Override
    public BusinessVerification save(BusinessVerification businessVerification) {
        return jpaRepository.save(BusinessVerificationJpaEntity.fromDomain(businessVerification)).toDomain();
    }

    @Override
    public Optional<BusinessVerification> findLatestByMemberId(Long memberId) {
        return jpaRepository.findFirstByMemberIdOrderByVerifiedAtDescIdDesc(memberId)
                .map(BusinessVerificationJpaEntity::toDomain);
    }

}
