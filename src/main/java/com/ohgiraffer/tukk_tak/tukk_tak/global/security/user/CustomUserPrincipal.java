package com.ohgiraffer.tukk_tak.tukk_tak.global.security.user;

import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Member;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.MemberStatus;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/*
 * comment.
 *  Spring Security가 사용하는 "현재 로그인 사용자" 객체
 *  Redis 캐시에서 복원한 경우에는 password가 null이다.
 * */

public class CustomUserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;      // 캐시에서 복원한 principal은 null
    private final Role role;
    private final boolean enabled;

    private CustomUserPrincipal(Long id, String email, String password, Role role, boolean enabled) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
    }

    public static CustomUserPrincipal from(Member member) {
        return new CustomUserPrincipal(member.getId(), member.getEmail(), member.getPassword(),
                member.getRole(), member.getStatus() == MemberStatus.ACTIVE);
    }

    public static CustomUserPrincipal ofCached(Long id, String email, Role role, MemberStatus status) {
        return new CustomUserPrincipal(id, email, null, role, status == MemberStatus.ACTIVE);
    }

    public Long getId() { return id; }
    public Role getRole() { return role; }

    @Override public String getUsername() { return email; }
    @Override public String getPassword() { return password; }
    @Override public boolean isEnabled() { return enabled; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

}
