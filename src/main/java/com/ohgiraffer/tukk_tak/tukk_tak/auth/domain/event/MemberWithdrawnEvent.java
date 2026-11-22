package com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.event;

/*
 * comment.
 *  회원 탈퇴 이벤트
 *  탈퇴가 끝난 뒤 발행한다. 사이트, 커뮤니티, 캐시 정리가 필요한 도메인은 이 이벤트를 받아서 처리한다.
 * */

public record MemberWithdrawnEvent(
        Long memberId   // 탈퇴한 회원
) {
}
