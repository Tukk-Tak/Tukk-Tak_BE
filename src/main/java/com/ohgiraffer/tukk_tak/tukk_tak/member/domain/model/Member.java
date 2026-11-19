package com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;

/*
 * comment.
 *  회원 도메인 모델 (DB를 모르는 순수 자바 객체)
 *  구글로 가입한 회원은 password가 null이다.
 *  hasPassword(), isActive() 같은 회원 관련 규칙을 가진다.
 * */

@Getter
public class Member {

    private final Long id;
    private final String email;
    private String password;            // 구글 가입자는 null
    private final LoginType loginType;
    private String nickname;
    private final String name;          // 가입 후 수정 불가
    private String phone;
    private String businessPhone;
    private String address;
    private final Role role;
    private final boolean emailVerified;
    private final boolean privacyAgreed;
    private final boolean aiUsageAgreed;
    private MemberStatus status;
    private LocalDateTime withdrawnAt;

    public Member(Long id, String email, String password, LoginType loginType,
                  String nickname, String name, String phone, String businessPhone,
                  String address, Role role, boolean emailVerified, boolean privacyAgreed,
                  boolean aiUsageAgreed, MemberStatus status, LocalDateTime withdrawnAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.loginType = loginType;
        this.nickname = nickname;
        this.name = name;
        this.phone = phone;
        this.businessPhone = businessPhone;
        this.address = address;
        this.role = role;
        this.emailVerified = emailVerified;
        this.privacyAgreed = privacyAgreed;
        this.aiUsageAgreed = aiUsageAgreed;
        this.status = status;
        this.withdrawnAt = withdrawnAt;
    }

    /** 관리자 계정은 가입 API 없이 별도로 지급. NOT NULL 컬럼은 고정값으로 채움 */
    public static Member admin(String email, String encodedPassword) {
        return new Member(null, email, encodedPassword, LoginType.LOCAL,
                "관리자", "관리자", "00000000000", null, null,
                Role.ADMIN, true, true, true, MemberStatus.ACTIVE, null);
    }

    public boolean hasPassword() { return password != null && !password.isBlank(); }
    public boolean isActive() { return status == MemberStatus.ACTIVE; }

}
