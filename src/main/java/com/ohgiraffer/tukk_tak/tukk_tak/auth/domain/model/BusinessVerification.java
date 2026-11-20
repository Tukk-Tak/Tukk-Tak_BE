package com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * comment.
 *  business_verification 테이블의 최소 엔티티
 *  지금은 "이미 인증된 사업자번호인가"를 조회하는 데만 쓴다.
 *  나머지 컬럼(member_id, open_date, ceo_name, status 등)과 저장은 5번 가입 API에서 이 엔티티를 확장하면서 넣는다.
 *  (APPROVED만 저장하기로 했으므로 이 테이블에 있다는 것 자체가 인증 완료다)
 * */

@Getter
@Entity
@Table(name = "business_verification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)   // JPA용 기본 생성자 (외부에서 빈 객체를 만들지 못하게 막는다)
public class BusinessVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 숫자 10자리로 정규화된 사업자번호 (DDL은 VARCHAR(20))
    @Column(name = "business_number", nullable = false, length = 20)
    private String businessNumber;

}
