package com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.usecase;

import com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.result.AiUsageReceipt;

/*
 * comment.
 *  플랜별 한도를 검사하는 입구 (다른 도메인이 AI 사용, 저장, 배포, 수정 전에 호출한다)
 *  구현은 PlanPolicyService가 맡고, 호출하는 쪽은 이 인터페이스만 안다.
 *  한도를 넘으면 PlanException을 던지고, HTTP 변환은 presentation의 PlanExceptionHandler가 한다.
 * */

public interface PlanPolicyUsecase {

    // AI를 쓸 수 있는지만 확인한다 (횟수는 올리지 않음. 화면에서 버튼을 막을 때 쓴다)
    void checkCanUseAi(Long memberId);

    // AI 사용 횟수를 1 올린다 (AI 호출 전에 부른다). 실패하면 반환된 영수증으로 releaseAiUsage를 부른다
    AiUsageReceipt recordAiUsage(Long memberId);

    // AI 호출이 실패했을 때 올렸던 횟수를 되돌린다 (실패한 시도가 횟수를 깎지 않게)
    void releaseAiUsage(AiUsageReceipt receipt);

    // 사이트를 새로 저장할 수 있는지 확인한다 (currentSavedCount는 지금 저장된 사이트 수)
    void checkCanSave(Long memberId, int currentSavedCount);

    // 사이트를 배포할 수 있는지 확인한다 (currentPublishedCount는 지금 배포 중인 사이트 수)
    void checkCanPublish(Long memberId, int currentPublishedCount);

    // 사이트를 수정할 수 있는 플랜인지 확인한다
    void checkCanEdit(Long memberId);

}
