package com.ohgiraffer.tukk_tak.tukk_tak.site.application.policy;

import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.exception.AiDraftLimitExceededException;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanLimit;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;
import org.springframework.stereotype.Component;

@Component
public class AiDraftPolicy {

    public void validateGenerateLimit(int currentUsedCount, PlanName plan) {
        int limit = PlanLimit.of(plan).aiLimit();
        if (currentUsedCount >= limit) {
            String message = (plan == PlanName.FREE)
                    ? "무료 플랜은 AI 초안을 " + limit + "회만 만들 수 있어요."
                    : "이번 달 AI 초안은 최대 " + limit + "회까지 만들 수 있어요.";
            throw new AiDraftLimitExceededException(message);
        }
    }
}
