package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.command.PreparePaymentCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.port.PaymentGatewayPort;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.port.PendingPaymentPort;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.KakaoReadyResult;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.PreparePaymentResult;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.usecase.PaymentCommandUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.exception.PaymentException;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentMethod;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PendingPayment;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.application.policy.PlanPrice;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.model.PlanName;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/*
 * comment.
 *  결제 명령 서비스 (지금은 결제 준비만 구현)
 *  - 클래스에 @Transactional을 걸지 않았다.
 *    결제 준비는 DB를 읽기만 하고, 카카오페이 호출(외부 네트워크)이 끼어 있어서
 *    트랜잭션을 열면 응답을 기다리는 동안 DB 커넥션을 붙잡고 있게 된다.
 *  - 임시 주문은 Redis에 있어서 DB 트랜잭션과 무관하다.
 * */

@Service
@RequiredArgsConstructor
public class PaymentCommandService implements PaymentCommandUsecase {

    // 서버 시간대와 상관없이 한국 날짜 기준으로 구독 기간을 계산한다
    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
    // 주문번호 접두어 (토스 규칙: 6~64자, 영문/숫자/-/_ 만 가능. 접두어 5자 + UUID 32자 = 37자)
    private static final String ORDER_ID_PREFIX = "TUKK-";

    private final SubscriptionRepository subscriptionRepository;
    private final PaymentGatewayPort paymentGatewayPort;
    private final PendingPaymentPort pendingPaymentPort;

    // 한 번에 살 수 있는 최대 개월 수 (제한은 없지만 터무니없는 값은 막는 안전장치)
    @Value("${payment.max-months:24}") private int maxMonths;
    // 임시 주문 유지 시간(분). 토스는 승인 API를 successUrl 이동 후 10분 안에 호출해야 해서 그보다 길게 둔다
    @Value("${payment.pending-ttl-minutes:15}") private long pendingTtlMinutes;

    /*
     * comment.
     *  결제 준비
     *  금액과 기간은 서버가 계산한다. 카카오페이는 여기서 ready를 호출해 이동 주소를 받아 온다.
     *  임시 주문은 회원당 1개이고, 다시 준비하면 이전 주문을 덮어쓴다. (돈은 승인 전까지 움직이지 않아 안전하다)
     * */
    @Override
    public PreparePaymentResult preparePayment(PreparePaymentCommand command) {
        PlanName planName = command.planName();

        // 1. FREE는 판매하는 상품이 아니다
        if (planName == PlanName.FREE) {
            throw PaymentException.planNotPurchasable();
        }

        // 2. 개월 수 범위 확인 (프론트 검증은 우회할 수 있어서 서버에서도 본다)
        if (command.months() < 1 || command.months() > maxMonths) {
            throw PaymentException.monthsInvalid();
        }

        // 3. 금액은 프론트 값을 믿지 않고 서버가 계산 (1개월 가격 x 개월 수)
        int amount = Math.multiplyExact(PlanPrice.of(planName).monthlyPrice(), command.months());

        // 4. 예상 기간: 같거나 높은 플랜의 가장 늦은 만료일 다음 날부터 (없으면 오늘부터)
        LocalDate startDate = resolveStartDate(command.memberId(), planName);
        LocalDate endDate = startDate.plusMonths(command.months()).minusDays(1);

        // 5. 주문번호와 상품명 생성 (주문번호는 payment.order_id UNIQUE라 겹치지 않게 UUID를 쓴다)
        String orderId = ORDER_ID_PREFIX + UUID.randomUUID().toString().replace("-", "");
        String orderName = "툭탁 " + planName.name() + " " + command.months() + "개월";

        // 6. 카카오페이는 ready 호출로 tid와 이동 주소를 받는다 (토스는 프론트가 결제위젯으로 처리)
        KakaoReadyResult ready = null;
        if (command.method() == PaymentMethod.KAKAOPAY) {
            ready = paymentGatewayPort.readyKakao(orderId, command.memberId(), orderName, amount);
        }

        // 7. 임시 주문 저장 (승인 단계에서 주문번호와 금액을 대조하는 기준)
        //    PG 호출이 성공한 뒤에 저장해서, 호출이 실패하면 쓸모없는 임시 주문이 남지 않는다
        pendingPaymentPort.save(command.memberId(),
                new PendingPayment(orderId, command.method(), planName, command.months(), amount,
                        ready == null ? null : ready.tid()),
                Duration.ofMinutes(pendingTtlMinutes));

        if (ready == null) {
            return PreparePaymentResult.toss(orderId, orderName, amount, startDate, endDate);
        }
        return PreparePaymentResult.kakao(orderId, orderName, amount, startDate, endDate,
                ready.redirectPcUrl(), ready.redirectMobileUrl());
    }

    // 시작일: 구매 플랜과 같거나 높은 플랜 중 가장 늦은 만료일 + 1일 (예약된 구독 포함), 없으면 오늘
    private LocalDate resolveStartDate(Long memberId, PlanName purchased) {
        LocalDate today = LocalDate.now(SEOUL);
        List<PlanName> sameOrHigher = Arrays.stream(PlanName.values())
                .filter(plan -> plan.compareTo(purchased) >= 0)
                .toList();
        return subscriptionRepository.findLatestEndDate(memberId, sameOrHigher, today)
                .map(latestEnd -> latestEnd.plusDays(1))
                .orElse(today);
    }

}
