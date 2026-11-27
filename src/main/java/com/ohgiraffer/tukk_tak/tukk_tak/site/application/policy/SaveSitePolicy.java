package com.ohgiraffer.tukk_tak.tukk_tak.site.application.policy;

import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.exception.SiteLimitExceededException;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanLimit;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;
import org.springframework.stereotype.Component;

@Component
public class SaveSitePolicy {

    public void validateSaveLimit(long currentSavedCount, PlanName plan) {
        int saveLimit = PlanLimit.of(plan).saveLimit();
        if (currentSavedCount >= saveLimit) {
            throw new SiteLimitExceededException("저장할 수 있는 사이트는 최대 " + saveLimit + "개예요.");
        }
    }
}