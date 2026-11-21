package com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.auth.domain.repository.BusinessVerificationRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.exception.PlanException;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.policy.PlanLimits;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.result.AiUsageReceipt;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.usecase.PlanPolicyUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.Subscription;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;

/*
 * comment.
 *  플랜별 한도 검사 구현체
 *  현재 플랜은 "오늘 이용 중인 구독 중 가장 높은 플랜"이고, 유료가 없거나 만료됐으면 항상 있는 FREE가 된다.
 *  현재 저장/배포 개수는 사이트 테이블을 모르는 이 서비스가 세지 않고, 호출하는 쪽이 넘긴다.
 *  한도를 넘으면 PlanException을 던진다.
 * */

@Service
@RequiredArgsConstructor
public class PlanPolicyService implements PlanPolicyUsecase {

    // 서버 시간대와 상관없이 한국 날짜 기준으로 구독 기간을 판단한다
    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private final SubscriptionRepository subscriptionRepository;
    private final BusinessVerificationRepository businessVerificationRepository;

    @Override
    public void checkCanUseAi(Long memberId) {

        // 1. 현재 구독과 이번 주기 계산
        LocalDate today = LocalDate.now(SEOUL);
        Subscription current = currentSubscription(memberId, today);
        PlanLimits limits = PlanLimits.of(current.getPlanName());
        LocalDate cycleStart = aiCycleStart(current, limits, today);

        // 2. 이번 주기에 쓴 횟수가 한도 이상이면 거절
        if (current.aiUsageIn(cycleStart) >= limits.aiUsageLimit()) {
            throw PlanException.aiLimitExceeded();
        }
    }

    @Override
    public AiUsageReceipt recordAiUsage(Long memberId) {

        // 1. 현재 구독과 이번 주기 계산
        LocalDate today = LocalDate.now(SEOUL);
        Subscription current = currentSubscription(memberId, today);
        PlanLimits limits = PlanLimits.of(current.getPlanName());
        LocalDate cycleStart = aiCycleStart(current, limits, today);

        // 2. 한 번의 UPDATE로 "한도 미만일 때만 +1" (동시에 눌러도 한도를 넘지 않고, 주기가 바뀌었으면 0부터 센다)
        if (!subscriptionRepository.increaseAiUsage(current.getId(), cycleStart, limits.aiUsageLimit())) {
            throw PlanException.aiLimitExceeded();
        }
        return new AiUsageReceipt(current.getId(), cycleStart);
    }

    @Override
    public void releaseAiUsage(AiUsageReceipt receipt) {
        subscriptionRepository.decreaseAiUsage(receipt.subscriptionId(), receipt.cycleStart());
    }

    @Override
    public void checkCanSave(Long memberId, int currentSavedCount) {
        PlanLimits limits = currentLimits(memberId);
        if (currentSavedCount >= limits.saveLimit()) {
            throw PlanException.saveLimitExceeded();
        }
    }

    @Override
    public void checkCanPublish(Long memberId, int currentPublishedCount) {

        // 1. 플랜이 배포를 허용하는지 (FREE는 한도가 0이라 여기서 거절. 업그레이드를 안내하기 위해 가장 먼저 본다)
        PlanLimits limits = currentLimits(memberId);
        if (limits.publishLimit() == 0) {
            throw PlanException.publishNotAllowed();
        }

        // 2. 사업자 인증을 마친 회원만 배포 가능 (APPROVED만 저장하므로 기록이 있으면 인증된 것)
        if (!businessVerificationRepository.existsByMemberId(memberId)) {
            throw PlanException.businessNotVerified();
        }

        // 3. 배포 개수 한도
        if (currentPublishedCount >= limits.publishLimit()) {
            throw PlanException.publishLimitExceeded();
        }
    }

    @Override
    public void checkCanEdit(Long memberId) {
        if (!currentLimits(memberId).canEdit()) {
            throw PlanException.editNotAllowed();
        }
    }

    // 현재 플랜의 한도
    private PlanLimits currentLimits(Long memberId) {
        Subscription current = currentSubscription(memberId, LocalDate.now(SEOUL));
        return PlanLimits.of(current.getPlanName());
    }

    // 오늘 이용 중인 구독 중 가장 높은 플랜 (예약 구독은 시작일 전이라 제외되고, 만료된 유료는 FREE로 내려간다)
    private Subscription currentSubscription(Long memberId, LocalDate today) {
        return subscriptionRepository.findInUse(memberId, today).stream()
                .max(Comparator.comparing(Subscription::getPlanName))
                .orElseThrow(() -> new IllegalStateException("이용 중인 구독이 없습니다. memberId=" + memberId));
    }

    // AI 횟수를 세는 주기의 시작일: 월 리셋 플랜은 오늘이 속한 월 주기, FREE는 시작일 고정(리셋 없이 평생 한도)
    private LocalDate aiCycleStart(Subscription subscription, PlanLimits limits, LocalDate today) {
        return limits.aiResets() ? subscription.cycleStartOf(today) : subscription.getStartDate();
    }


}
