package com.ohgiraffer.tukk_tak.tukk_tak.subscription.infrastructure.adapter;

import com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.port.SiteUsagePort;

/*
 * comment.
 *  SiteUsagePort의 임시 구현 (사이트 도메인이 생기기 전까지 항상 0을 돌려준다)
 *  사이트 도메인에서 진짜 구현체를 만들면 이 클래스를 지워야 한다. (같은 타입의 빈이 둘이면 서버가 뜨지 않는다)
 * */

public class SiteUsageStubAdapter implements SiteUsagePort {

    @Override
    public int countSaved(Long memberId) {
        return 0;
    }

    @Override
    public int countPublished(Long memberId) {
        return 0;
    }

}
