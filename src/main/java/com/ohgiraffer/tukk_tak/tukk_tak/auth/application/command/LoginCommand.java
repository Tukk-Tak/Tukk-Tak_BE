package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.command;

/*
 * comment.
 *  로그인 요청을 application 계층으로 넘기는 명령 객체
 *  (presentation의 LoginRequest를 application이 직접 알지 못하게 분리)
 * */

public record LoginCommand(

        String email,          // 입력한 이메일
        String password,       // 입력한 비밀번호
        boolean rememberMe,    // "로그인 상태 유지" 체크 여부
        String clientIp        // 실패 로그에 남길 클라이언트 IP

) {
}
