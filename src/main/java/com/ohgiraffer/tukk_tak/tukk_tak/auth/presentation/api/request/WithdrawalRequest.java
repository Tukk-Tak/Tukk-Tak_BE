package com.ohgiraffer.tukk_tak.tukk_tak.auth.presentation.api.request;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command.WithdrawalCommand;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;

/*
 * comment.
 *  회원 탈퇴 요청
 *  자체 가입 회원은 password, 구글 가입 회원은 credential을 보낸다. (어느 쪽이 필요한지는 서버가 회원 정보로 정한다)
 *  개인정보 1년 보관에 동의하지 않으면 컨트롤러에 들어오기 전에 400으로 걸러낸다.
 * */

public record WithdrawalRequest(
        // 로그인과 같은 상한(64)만 둔다 (규칙 검사는 하지 않고 일치 여부만 본다)
        @Size(max = 64, message = "비밀번호가 너무 깁니다.")
        String password,                    // 현재 비밀번호 (자체 가입 회원)

        @Size(max = 4096, message = "구글 인증 정보가 너무 깁니다.")
        String credential,                  // 새로 받은 구글 ID 토큰 (구글 가입 회원)

        @AssertTrue(message = "개인정보 1년 보관에 동의해야 탈퇴할 수 있어요.")
        boolean retentionAgreed             // 개인정보 1년 보관 동의 (true만 허용)
) {
    // 요청 DTO -> 명령 객체 (회원 id는 요청 본문이 아니라 토큰에서 꺼낸 값을 쓴다)
    public WithdrawalCommand toCommand(Long memberId) {
        return new WithdrawalCommand(memberId, password, credential);
    }
}
