package com.ohgiraffer.tukk_tak.tukk_tak.global.security.jwt;

import com.ohgiraffer.tukk_tak.tukk_tak.global.security.token.TokenBlacklistService;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.user.CustomUserDetailsService;
import com.ohgiraffer.tukk_tak.tukk_tak.global.security.user.CustomUserPrincipal;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
 * comment.
 *  모든 요청 앞에서 access token을 확인하는 인증 필터
 *  토큰이 유효하고 블랙리스트에 없으면 "로그인한 사용자"로 등록한다.
 *  토큰이 없거나 잘못됐을 때는 인증 없이 통과시키고, 접근 제한은 SecurityConfig가 판단한다.
 * */

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtTokenProvider.resolveToken(request);
        if (accessToken != null) {
            Claims claims = jwtTokenProvider.resolveAccessClaims(accessToken);
            if (claims != null) {
                authenticate(request, claims);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void authenticate(HttpServletRequest request, Claims claims) {
        if (tokenBlacklistService.isBlacklisted(jwtTokenProvider.extractJti(claims))) {
            request.setAttribute("ALREADY_LOGGED_OUT", true);
            return;
        }
        try {
            CustomUserPrincipal principal =
                    userDetailsService.loadByMemberId(jwtTokenProvider.extractMemberId(claims));
            if (principal.isEnabled()) {
                var authentication = new UsernamePasswordAuthenticationToken(
                        principal, null, principal.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (UsernameNotFoundException | NumberFormatException e) {
            SecurityContextHolder.clearContext();
        }
    }

}
