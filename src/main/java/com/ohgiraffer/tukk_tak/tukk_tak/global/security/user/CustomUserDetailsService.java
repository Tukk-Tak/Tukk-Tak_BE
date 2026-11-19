package com.ohgiraffer.tukk_tak.tukk_tak.global.security.user;

import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.Member;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
 * comment.
 *  회원 정보를 읽어 CustomUserPrincipal로 만들어 주는 서비스
 *  loadByMemberId()는 캐시를 먼저 확인하고, 없을 때만 DB를 조회한다.
 * */

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PrincipalCache principalCache;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));
        return CustomUserPrincipal.from(member);
    }

    /** 인증 필터용. 캐시 hit이면 DB를 조회하지 않음 */
    public CustomUserPrincipal loadByMemberId(Long memberId) {
        return principalCache.get(memberId).orElseGet(() -> {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));
            principalCache.put(member);
            return CustomUserPrincipal.from(member);
        });
    }

}
