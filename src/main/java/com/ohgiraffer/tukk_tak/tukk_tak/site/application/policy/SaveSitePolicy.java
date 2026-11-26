package com.ohgiraffer.tukk_tak.tukk_tak.site.application.policy;

import com.ohgiraffer.tukk_tak.tukk_tak.site.domain.exception.SiteLimitExceededException;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;
import org.springframework.stereotype.Component;

@Component
public class SaveSitePolicy {

    private static final int FREE_SAVE_LIMIT = 3;
    private static final int BASIC_SAVE_LIMIT = 5;
    private static final int PREMIUM_SAVE_LIMIT = 10;

    // 최초 저장일 때, 저장된 개수가 플랜의 한도 이상이면 막음
    public void validateSaveLimit(long currentSavedCount, PlanName plan) {
        int saveLimit = switch (plan) {
            case FREE -> FREE_SAVE_LIMIT;
            case BASIC -> BASIC_SAVE_LIMIT;
            case PREMIUM -> PREMIUM_SAVE_LIMIT;
        };

        if (currentSavedCount >= saveLimit) {
            throw new SiteLimitExceededException("저장할 수 있는 사이트는 최대 " + saveLimit + "개예요.");
        }
    }
}