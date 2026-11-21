package com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.model;

/*
 * comment.
 *  구글 ID 토큰 검증을 마친 사용자 정보
 *  이메일 인증 여부는 검증기가 이미 확인했으므로 인증된 이메일만 담긴다.
 * */

public record GoogleIdentity(
        String email,
        String name       // 구글 프로필 이름 (없으면 빈 문자열
) {
}
