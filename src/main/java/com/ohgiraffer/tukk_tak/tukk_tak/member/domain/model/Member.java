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
    private String name;                // 복구 가입 때만 바뀜 (그 외에는 가입 후 수정 불가)
    private String phone;
    private String businessPhone;
    private String address;
    private final Role role;
    private final boolean emailVerified;
    private boolean privacyAgreed;      // 복구 가입 때 다시 동의받아 갱신
    private boolean aiUsageAgreed;      // 복구 가입 때 다시 동의받아 갱신
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

    /** 자체 가입 회원. 가입 경로 LOCAL, 권한 BUSINESS, 이메일은 인증을 마친 상태. password는 암호화된 값 */
    public static Member local(String email, String encodedPassword, String nickname, String name,
                               String phone, String businessPhone, String address,
                               boolean privacyAgreed, boolean aiUsageAgreed) {
        return new Member(null, email, encodedPassword, LoginType.LOCAL, nickname, name, phone,
                businessPhone, address, Role.BUSINESS, true, privacyAgreed, aiUsageAgreed,
                MemberStatus.ACTIVE, null);
    }

    /** 구글 가입 회원. 가입 경로 GOOGLE, 권한 BUSINESS, 비밀번호 없음, 이메일은 구글이 인증한 상태 */
    public static Member google(String email, String nickname, String name, String phone,
                                String businessPhone, String address,
                                boolean privacyAgreed, boolean aiUsageAgreed) {
        return new Member(null, email, null, LoginType.GOOGLE, nickname, name, phone,
                businessPhone, address, Role.BUSINESS, true, privacyAgreed, aiUsageAgreed,
                MemberStatus.ACTIVE, null);
    }

    public boolean hasPassword() { return password != null && !password.isBlank(); }
    public boolean isActive() { return status == MemberStatus.ACTIVE; }
    public boolean isWithdrawn() { return status == MemberStatus.WITHDRAWN; }

    // 탈퇴한 회원을 같은 행으로 되살린다 (member id가 같아야 예전 사이트와 구독이 그대로 이어짐)
    // 구글 회원은 encodedPassword에 null을 넘긴다
    public void restore(String encodedPassword, String nickname, String name, String phone,
                        String businessPhone, String address, boolean privacyAgreed, boolean aiUsageAgreed) {
        if (!isWithdrawn()) {
            throw new IllegalStateException("탈퇴한 회원만 복구할 수 있습니다.");
        }
        this.password = encodedPassword;
        this.nickname = nickname;
        this.name = name;
        this.phone = phone;
        this.businessPhone = businessPhone;
        this.address = address;
        this.privacyAgreed = privacyAgreed;
        this.aiUsageAgreed = aiUsageAgreed;
        this.status = MemberStatus.ACTIVE;
        this.withdrawnAt = null;
    }

}
