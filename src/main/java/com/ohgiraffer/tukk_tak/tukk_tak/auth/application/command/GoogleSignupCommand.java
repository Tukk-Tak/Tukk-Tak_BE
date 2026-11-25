package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command;

/*
 * comment.
 *  구글 회원가입 명령
 *  이메일은 받지 않고 signupTicket에서 꺼낸다. 비밀번호가 없는 가입이다.
 * */

public record GoogleSignupCommand(
        String signupTicket,      // 구글 로그인 때 받은 가입 티켓
        String name,              // 이름 (사업자 인증을 했다면 대표자명과 같아야 함)
        String nickname,
        String phone,             // 개인 전화번호
        boolean privacyAgreed,    // 개인정보 수집·이용 동의 (필수)
        boolean aiUsageAgreed,    // AI 사용 동의 (선택, 마이페이지에서 바꿀 수 있음)
        String businessToken,     // 사업자 확인 토큰 (선택)
        String businessPhone,     // 업장 전화번호 (선택)
        String address            // 업장 주소 (선택)
) {
}
