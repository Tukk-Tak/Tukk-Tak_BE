package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.ConfirmPaymentResult;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.PgApproval;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.Payment;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PendingPayment;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.repository.PaymentRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.Subscription;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

/*
 * comment.
 *  PG 승인이 끝난 결제를 DB에 반영하는 곳 (구독 재배치, 구독 저장, 결제 저장을 한 트랜잭션으로)
 *  서비스가 PG 호출 동안 DB 커넥션을 잡지 않도록, 트랜잭션은 이 클래스의 메서드에서만 시작한다.
 * */

@Component
@RequiredArgsConstructor
public class PaymentRegistrar {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public ConfirmPaymentResult register(Long memberId, PendingPayment pending, PgApproval approval) {
        // 1. 시작일을 다시 계산한다 (준비 이후에 구독 상태가 바뀌었을 수 있어서 이 시점 기준으로 확정한다)
        LocalDate today = LocalDate.now(SEOUL);
        List<PlanName> samePlanOrHigher = Arrays.stream(PlanName.values())
                .filter(plan -> plan.compareTo(pending.planName()) >= 0)
                .toList();
        LocalDate startDate = subscriptionRepository.findLatestEndDate(memberId, samePlanOrHigher, today)
                .map(latestEnd -> latestEnd.plusDays(1))
                .orElse(today);

        // 2. 종료일을 계산한다 (시작일 + N개월 - 1일, 종료일 당일까지 이용)
        LocalDate endDate = startDate.plusMonths(pending.months()).minusDays(1);

        // 3. 낮은 플랜의 남은 기간을 새 이용 기간만큼 뒤로 민다 (환불이 없어서 낮은 플랜의 남은 기간이 사라지지 않게 보존한다)
        List<PlanName> lowerPlans = Arrays.stream(PlanName.values())
                .filter(plan -> plan != PlanName.FREE && plan.compareTo(pending.planName()) < 0)
                .toList();
        if (!lowerPlans.isEmpty()) {
            int days = (int) (ChronoUnit.DAYS.between(startDate, endDate) + 1);
            subscriptionRepository.pauseLowerPlans(memberId, lowerPlans, startDate, days);
        }

        // 4. 새 구독을 저장한다 (결제할 때마다 새 행을 만든다)
        Subscription subscription = subscriptionRepository.save(
                Subscription.paid(memberId, pending.planName(), startDate, endDate));

        // 5. 결제 내역을 저장한다 (구독 행과 연결, 성공한 결제만 저장)
        LocalDateTime paidAt = LocalDateTime.now(SEOUL);
        paymentRepository.save(Payment.paid(subscription.getId(), memberId, pending.amount(),
                pending.method(), pending.orderId(), approval.paymentKey(), pending.months(), paidAt));

        return new ConfirmPaymentResult(pending.orderId(), pending.method(), pending.planName(),
                pending.months(), pending.amount(), startDate, endDate, paidAt);
    }

}
