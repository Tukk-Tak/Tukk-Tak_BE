package com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.policy.PlanLimits;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.port.SiteUsagePort;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.result.SubscriptionInfoResult;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.usecase.SubscriptionQueryUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.Subscription;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

/*
 * comment.
 *  구독 조회 서비스
 *  현재 플랜은 PlanPolicyService와 같은 기준(오늘 이용 중인 구독 중 가장 높은 플랜)으로 정한다.
 *  끝나지 않은 구독을 한 번에 읽어 메모리에서 현재, 연장분, 대기로 나눠서 쿼리를 늘리지 않는다.
 * */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionQueryService implements SubscriptionQueryUsecase {

    // 서버 시간대와 상관없이 한국 날짜 기준으로 구독 기간을 판단한다
    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private final SubscriptionRepository subscriptionRepository;
    private final SiteUsagePort siteUsagePort;

    @Override
    public SubscriptionInfoResult getMySubscription(Long memberId) {
        // 1. 끝나지 않은 구독을 시작일 순으로 한 번에 가져온다 (현재, 연장분, 대기를 모두 여기서 나눈다)
        LocalDate today = LocalDate.now(SEOUL);
        List<Subscription> unexpired = subscriptionRepository.findUnexpired(memberId, today);

        // 2. 현재 구독을 정한다 (오늘 이용 중인 구독 중 가장 높은 플랜, 없으면 FREE로 보여준다)
        //    데이터가 어긋난 회원도 화면이 깨지지 않게 이 조회에서만 FREE로 대신하고 로그를 남긴다
        Subscription current = unexpired.stream()
                .filter(s -> !s.getStartDate().isAfter(today))
                .max(Comparator.comparing(Subscription::getPlanName))
                .orElseGet(() -> {
                    log.warn("[subscription-query] 이용 중인 구독 없음, FREE로 대체 | memberId={}", memberId);
                    return Subscription.free(memberId, today);
                });

        // 3. 같은 플랜을 이어서 연장한 구독은 하나의 이용 기간으로 합친다 (종료일 다음 날에 시작하는 같은 플랜만)
        LocalDate periodEnd = current.getEndDate();
        Set<Long> mergedIds = new HashSet<>();
        for (Subscription s : unexpired) {
            if (s.getPlanName() == current.getPlanName() && s.getStartDate().equals(periodEnd.plusDays(1))) {
                periodEnd = s.getEndDate();
                mergedIds.add(s.getId());
            }
        }

        // 4. 대기 중인 구독을 고른다 (현재와 합쳐진 연장분, FREE는 제외)
        //    시작일이 지났는데 현재가 아니면 상위 플랜 때문에 멈춰 있는 구독이다
        List<SubscriptionInfoResult.Queued> queued = unexpired.stream()
                .filter(s -> s.getPlanName() != PlanName.FREE)
                .filter(s -> !Objects.equals(s.getId(), current.getId()))
                .filter(s -> !mergedIds.contains(s.getId()))
                .map(s -> new SubscriptionInfoResult.Queued(s.getPlanName(), s.getStartDate(),
                        s.getEndDate(), !s.getStartDate().isAfter(today)))
                .toList();

        // 5. 현재 구독 정보를 만든다 (FREE는 만료가 없어 종료일과 남은 일수를 비운다)
        boolean unlimited = current.getPlanName() == PlanName.FREE;
        SubscriptionInfoResult.Current currentInfo = new SubscriptionInfoResult.Current(
                current.getPlanName(), current.getStartDate(),
                unlimited ? null : periodEnd,
                unlimited ? null : (int) (ChronoUnit.DAYS.between(today, periodEnd) + 1),
                unlimited);

        // 6. 한도와 사용량을 만든다 (AI 주기는 PlanPolicyService와 같은 방식: 월 리셋 플랜은 월 주기, FREE는 시작일 고정)
        PlanLimits limits = PlanLimits.of(current.getPlanName());
        LocalDate cycleStart = limits.aiResets() ? current.cycleStartOf(today) : current.getStartDate();
        SubscriptionInfoResult.Usage usage = new SubscriptionInfoResult.Usage(
                current.aiUsageIn(cycleStart),
                siteUsagePort.countPublished(memberId),
                siteUsagePort.countSaved(memberId));

        return new SubscriptionInfoResult(currentInfo, queued, limits, usage);
    }

}
