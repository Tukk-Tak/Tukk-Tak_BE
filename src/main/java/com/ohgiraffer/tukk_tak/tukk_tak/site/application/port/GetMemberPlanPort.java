package com.ohgiraffer.tukk_tak.tukk_tak.site.application.port;

import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;

public interface GetMemberPlanPort {
    // 회원의 현재 구독 플랜
    PlanName getPlan(Long memberId);
}
