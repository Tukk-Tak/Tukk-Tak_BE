package com.ohgiraffer.tukk_tak.tukk_tak.auth.application.port;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.model.GoogleIdentity;

import java.time.Duration;
import java.util.Optional;

/*
 * comment.
 *  구글 가입 티켓 저장 포트(인터페이스)
 *  처음 온 구글 계정이 추가 정보를 입력하는 동안 인증된 구글 정보를 서버가 들고 있기 위한 임시 저장소다.
 * */

public interface GoogleSignupTicketPort {

    // 가입 티켓과 구글 정보를 저장 (ttl이 지나면 자동 삭제)
    void saveTicket(String ticket, GoogleIdentity identity, Duration ttl);

    // 가입 티켓으로 구글 정보를 꺼낸다 (없거나 만료됐으면 비어 있음)
    Optional<GoogleIdentity> findTicket(String ticket);

    // 가입 티켓을 지운다 (가입에 성공한 티켓의 재사용을 막는다)
    void deleteTicket(String ticket);

}
