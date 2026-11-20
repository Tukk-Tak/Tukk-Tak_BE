package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port;

import java.time.Duration;

/*
 * comment.
 *  이메일 인증번호 저장소 포트 (Redis)
 *  키 구성: email:code:{email} = 인증번호, email:cooldown:{email} = 재전송 대기
 * */

public interface EmailCodePort {

    // 재전송 대기 시간을 시작한다. 이미 대기 중이면 false (조회 후 저장이 아니라 한 번에 처리해서 연타에도 안전하다)
    boolean acquireCooldown(String email, Duration cooldown);

    // 인증번호를 저장한다 (기존 번호가 있으면 덮어쓴다)
    void saveCode(String email, String code, Duration ttl);

    // 인증번호와 재전송 대기를 모두 지운다 (메일 발송에 실패했을 때 되돌리는 용도)
    void clear(String email);

    // 저장된 인증번호를 꺼낸다 (없거나 만료됐으면 null)
    String findCode(String email);

    // 인증번호만 지운다 (인증에 성공한 번호의 재사용을 막는다. 재전송 대기는 그대로 둔다)
    void deleteCode(String email);

    // 인증 완료 토큰을 저장한다 (email:token:{token} = 이메일). 가입 요청 때 이 토큰으로 인증한 이메일을 알아낸다
    void saveToken(String token, String email, Duration ttl);

}
