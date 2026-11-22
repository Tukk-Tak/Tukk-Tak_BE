package com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;

/*
 * comment.
 *  회원 도메인 모델 (DB를 모르는 순수 자바 객체)
 *  구글로 가입한 회원은 password가 null이다.
 *  hasPassword(), isActive() 같은 회원 관련 규칙을 가진다.
 * */

@Getter
public class Member {

    // 동의 시각은 서버 시간대와 상관없이 한국 시간으로 기록한다
    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

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
    private LocalDateTime privacyAgreedAt;   // 개인정보 이용 동의 시각 (복구 가입 때 다시 동의하면 갱신)
    private LocalDateTime aiUsageAgreedAt;   // AI 사용 동의 시각 (동의가 바뀔 때만 갱신, 동의하지 않은 상태면 null)
    private MemberStatus status;
    private LocalDateTime withdrawnAt;

    public Member(Long id, String email, String password, LoginType loginType,
                  String nickname, String name, String phone, String businessPhone,
                  String address, Role role, boolean emailVerified, boolean privacyAgreed,
                  boolean aiUsageAgreed, LocalDateTime privacyAgreedAt, LocalDateTime aiUsageAgreedAt,
                  MemberStatus status, LocalDateTime withdrawnAt) {
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
        this.privacyAgreedAt = privacyAgreedAt;
        this.aiUsageAgreedAt = aiUsageAgreedAt;
        this.status = status;
        this.withdrawnAt = withdrawnAt;
    }

    /** 관리자 계정은 가입 API 없이 별도로 지급. NOT NULL 컬럼은 고정값으로 채움 */
    public static Member admin(String email, String encodedPassword) {
        return new Member(null, email, encodedPassword, LoginType.LOCAL,
                "관리자", "관리자", "00000000000", null, null,
                Role.ADMIN, true, true, true, LocalDateTime.now(SEOUL),
                LocalDateTime.now(SEOUL), MemberStatus.ACTIVE, null);
    }

    /** 자체 가입 회원. 가입 경로 LOCAL, 권한 BUSINESS, 이메일은 인증을 마친 상태. password는 암호화된 값 */
    public static Member local(String email, String encodedPassword, String nickname, String name,
                               String phone, String businessPhone, String address,
                               boolean privacyAgreed, boolean aiUsageAgreed) {
        return new Member(null, email, encodedPassword, LoginType.LOCAL, nickname, name, phone,
                businessPhone, address, Role.BUSINESS, true, privacyAgreed, aiUsageAgreed,
                LocalDateTime.now(SEOUL),agreedAt(aiUsageAgreed), MemberStatus.ACTIVE, null);
    }

    /** 구글 가입 회원. 가입 경로 GOOGLE, 권한 BUSINESS, 비밀번호 없음, 이메일은 구글이 인증한 상태 */
    public static Member google(String email, String nickname, String name, String phone,
                                String businessPhone, String address,
                                boolean privacyAgreed, boolean aiUsageAgreed) {
        return new Member(null, email, null, LoginType.GOOGLE, nickname, name, phone,
                businessPhone, address, Role.BUSINESS, true, privacyAgreed, aiUsageAgreed,
                LocalDateTime.now(SEOUL), LocalDateTime.now(SEOUL), MemberStatus.ACTIVE, null);
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
        this.privacyAgreedAt = agreedAt(privacyAgreed);   // 다시 동의받은 시각으로 갱신
        this.aiUsageAgreedAt = agreedAt(aiUsageAgreed);   // 다시 동의받은 시각으로 갱신
        this.status = MemberStatus.ACTIVE;
        this.withdrawnAt = null;
    }

    // 비밀번호를 바꾼다 (encodedPassword는 암호화된 값, 비밀번호가 없는 구글 회원에게는 쓸 수 없다)
    public void changePassword(String encodedPassword) {
        if (!hasPassword()) {
            throw new IllegalStateException("비밀번호가 없는 회원은 비밀번호를 바꿀 수 없습니다.");
        }
        this.password = encodedPassword;
    }

    // 탈퇴 처리: 데이터는 지우지 않고 상태만 바꾼다 (같은 이메일로 다시 가입하면 restore로 되살린다)
    public void withdraw() {
        if (!isActive()) {
            throw new IllegalStateException("활성 회원만 탈퇴할 수 있습니다.");
        }
        this.status = MemberStatus.WITHDRAWN;
        this.withdrawnAt = LocalDateTime.now(SEOUL);
    }

    // 마이페이지에서 바꿀 수 있는 정보를 반영한다 (이름과 이메일은 바꿀 수 없다. 바꾸지 않을 값은 현재 값을 그대로 넘긴다)
    public void updateProfile(String nickname, String phone, String businessPhone, String address) {
        this.nickname = nickname;
        this.phone = phone;
        this.businessPhone = businessPhone;
        this.address = address;
    }

    // AI 사용 동의 여부를 바꾼다 (끄면 AI 기능을 쓸 수 없다. 값이 실제로 바뀔 때만 동의 시각을 갱신한다)
    public void changeAiUsageAgreed(boolean agreed) {
        if (this.aiUsageAgreed == agreed) return;
        this.aiUsageAgreed = agreed;
        this.aiUsageAgreedAt = agreedAt(agreed);
    }

    // 동의했을 때만 지금 시각(한국 시간)을 기록한다
    private static LocalDateTime agreedAt(boolean agreed) {
        return agreed ? LocalDateTime.now(SEOUL) : null;
    }

}
