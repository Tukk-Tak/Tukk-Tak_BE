package com.ohgiraffer.tukk_tak.tukk_tak.member.application.port;

import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.model.BusinessTokenInfo;

import java.time.Duration;
import java.util.Optional;

/*
 * comment.
 *  사업자 확인 토큰 저장소 포트
 *  저장 방식(Redis)을 서비스가 몰라도 되게 한다.
 *  토큰을 꺼내 쓰고 지우는 메서드는 5번 가입 API를 만들 때 이 인터페이스에 추가한다.
 * */

public interface BusinessTokenPort {

    // 토큰과 사업자 정보를 함께 저장한다.
    // ttl이 지나면 자동으로 사라지므로 따로 지울 필요가 없다.
    void saveToken(String token, BusinessTokenInfo info, Duration ttl);

    // 사업자 확인 토큰으로 저장된 사업자 정보를 꺼낸다 (없거나 만료됐으면 비어 있음)
    Optional<BusinessTokenInfo> findToken(String token);

    // 사업자 확인 토큰을 지운다 (가입에 성공한 토큰의 재사용을 막는다)
    void deleteToken(String token);

}
