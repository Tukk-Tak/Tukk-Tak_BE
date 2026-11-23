package com.ohgiraffer.tukk_tak.tukk_tak.auth.infrastructure.persistence;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.model.BusinessVerification;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.model.BusinessVerifyStatus;
import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.model.VerifyMethod;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/*
 * comment.
 *  business_verification 테이블과 매핑되는 JPA 엔티티
 *  이 테이블에는 updated_at이 없어서 BaseTimeEntity를 상속하지 않는다. (상속하면 validate에서 컬럼 없음 오류)
 *  created_at은 DB 기본값이 채우고, document_url 등 이번에 안 쓰는 NULL 허용 컬럼은 매핑하지 않는다.
 * */

@Entity
@Table(name = "business_verification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessVerificationJpaEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "business_number", nullable = false, length = 20)
    private String businessNumber;

    @Column(name = "open_date", nullable = false)
    private LocalDate openDate;

    @Column(name = "ceo_name", nullable = false, length = 50)
    private String ceoName;

    @Enumerated(EnumType.STRING)
    @Column(name = "verify_method", nullable = false, length = 20)
    private VerifyMethod verifyMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BusinessVerifyStatus status;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    private BusinessVerificationJpaEntity(BusinessVerification b) {
        this.id = b.getId();
        this.memberId = b.getMemberId();
        this.businessNumber = b.getBusinessNumber();
        this.openDate = b.getOpenDate();
        this.ceoName = b.getCeoName();
        this.verifyMethod = b.getVerifyMethod();
        this.status = b.getStatus();
        this.verifiedAt = b.getVerifiedAt();
    }

    public static BusinessVerificationJpaEntity fromDomain(BusinessVerification b) { return new BusinessVerificationJpaEntity(b); }

    public BusinessVerification toDomain() {
        return new BusinessVerification(id, memberId, businessNumber, openDate, ceoName,
                verifyMethod, status, verifiedAt);
    }

}
