package com.ohgiraffer.tukk_tak.tukk_tak.member.infrastructure.persistence;

import com.ohgiraffer.tukk_tak.tukk_tak.global.entity.BaseTimeEntity;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.LoginType;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Member;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.MemberStatus;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*
 * comment.
 *  member 테이블과 매핑되는 JPA 엔티티
 *  BaseTimeEntity를 상속해서 생성/수정 시각이 자동으로 채워진다.
 *  수정 가능한 필드는 updateFrom()으로만 반영한다.
 * */

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberJpaEntity extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false, length = 20)
    private LoginType loginType;

    @Column(nullable = false, unique = true, length = 15)
    private String nickname;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(name = "business_phone", length = 20)
    private String businessPhone;

    @Column(length = 255)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Column(name = "privacy_agreed", nullable = false)
    private boolean privacyAgreed;

    @Column(name = "ai_usage_agreed", nullable = false)
    private boolean aiUsageAgreed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberStatus status;

    @Column(name = "withdrawn_at")
    private LocalDateTime withdrawnAt;

    private MemberJpaEntity(Member m) {
        this.id = m.getId();
        this.email = m.getEmail();
        this.password = m.getPassword();
        this.loginType = m.getLoginType();
        this.nickname = m.getNickname();
        this.name = m.getName();
        this.phone = m.getPhone();
        this.businessPhone = m.getBusinessPhone();
        this.address = m.getAddress();
        this.role = m.getRole();
        this.emailVerified = m.isEmailVerified();
        this.privacyAgreed = m.isPrivacyAgreed();
        this.aiUsageAgreed = m.isAiUsageAgreed();
        this.status = m.getStatus();
        this.withdrawnAt = m.getWithdrawnAt();
    }

    public static MemberJpaEntity fromDomain(Member member) { return new MemberJpaEntity(member); }

    public Member toDomain() {
        return new Member(id, email, password, loginType, nickname, name, phone,
                businessPhone, address, role, emailVerified, privacyAgreed,
                aiUsageAgreed, status, withdrawnAt);
    }

    /** 수정 가능한 필드만 반영 (email, loginType, role 등은 불변. name과 동의 값은 복구 가입 때만 바뀜) */
    public void updateFrom(Member m) {
        this.password = m.getPassword();
        this.nickname = m.getNickname();
        this.name = m.getName();
        this.phone = m.getPhone();
        this.businessPhone = m.getBusinessPhone();
        this.address = m.getAddress();
        this.privacyAgreed = m.isPrivacyAgreed();
        this.aiUsageAgreed = m.isAiUsageAgreed();
        this.status = m.getStatus();
        this.withdrawnAt = m.getWithdrawnAt();
    }

}
