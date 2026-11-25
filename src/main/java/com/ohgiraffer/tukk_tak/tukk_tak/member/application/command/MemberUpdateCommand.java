package com.ohgiraffer.tukk_tak.tukk_tak.member.application.command;

/*
 * comment.
 *  내 정보 수정 명령
 *  null이면 바꾸지 않겠다는 뜻이고, 업장 전화번호와 주소는 빈 문자열이면 삭제한다.
 * */

public record MemberUpdateCommand(
        Long memberId,            // 로그인한 회원 (토큰에서 꺼낸 값)
        String nickname,
        String phone,             // 개인 전화번호 (필수 항목이라 지울 수 없다)
        String businessPhone,     // 업장 전화번호 ("" 이면 삭제)
        String address,           // 업장 주소 ("" 이면 삭제)
        Boolean aiUsageAgreed     // AI 사용 동의
) {
}
