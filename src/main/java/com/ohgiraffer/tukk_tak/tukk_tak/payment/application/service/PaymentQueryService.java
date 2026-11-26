package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.command.QuotePaymentCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.PaymentHistoryResult;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.QuotePaymentResult;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.usecase.PaymentQueryUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.exception.PaymentException;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentHistoryPage;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.repository.PaymentRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.policy.PlanPrice;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

/*
 * comment.
 *  결제 조회 서비스 (지금은 결제 미리보기)
 *  시작일 규칙은 결제 준비와 같아야 해서 같은 기준(같거나 높은 플랜의 마지막 종료일 다음 날)을 쓴다.
 * */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentQueryService implements PaymentQueryUsecase {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;

    // 한 번에 살 수 있는 최대 개월 수 (피그마 기준 24개월)
    @Value("${payment.max-months:24}")
    private int maxMonths;

    @Override
    public QuotePaymentResult quotePayment(QuotePaymentCommand command) {
        // 1. FREE는 살 수 없는 플랜이다 (0원이라 결제 대상이 아님)
        if (command.planName() == PlanName.FREE) {
            throw PaymentException.planNotPurchasable();
        }

        // 2. 개월 수 범위를 검사한다 (상한은 설정값이라 요청 DTO가 아니라 여기서 본다)
        if (command.months() < 1 || command.months() > maxMonths) {
            throw PaymentException.monthsInvalid();
        }

        // 3. 금액을 계산한다 (Math.multiplyExact로 오버플로를 막는다)
        int monthlyPrice = PlanPrice.of(command.planName()).monthlyPrice();
        int amount = Math.multiplyExact(monthlyPrice, command.months());

        // 4. 시작일을 구한다 (같거나 높은 플랜의 마지막 종료일 다음 날, 없으면 오늘)
        LocalDate today = LocalDate.now(SEOUL);
        List<PlanName> samePlanOrHigher = Arrays.stream(PlanName.values())
                .filter(plan -> plan.compareTo(command.planName()) >= 0)
                .toList();
        LocalDate startDate = subscriptionRepository
                .findLatestEndDate(command.memberId(), samePlanOrHigher, today)
                .map(latestEnd -> latestEnd.plusDays(1))
                .orElse(today);

        // 5. 종료일을 구한다 (시작일 + N개월 - 1일, 종료일 당일까지 이용)
        LocalDate endDate = startDate.plusMonths(command.months()).minusDays(1);

        return new QuotePaymentResult(command.planName(), command.months(), monthlyPrice, amount,
                startDate, endDate);
    }

    @Override
    public PaymentHistoryResult getPaymentHistory(Long memberId, int page, int size) {
        // 1. 결제 내역을 최신순으로 한 페이지 가져온다 (결제와 구독을 한 번의 조인으로 읽는다)
        PaymentHistoryPage historyPage = paymentRepository.findHistory(memberId, page, size);

        // 2. 전체 페이지 수를 계산한다 (나머지가 있으면 한 페이지 더)
        int totalPages = (int) ((historyPage.totalElements() + size - 1) / size);

        return new PaymentHistoryResult(historyPage.content(), page, size,
                historyPage.totalElements(), totalPages);
    }

}
