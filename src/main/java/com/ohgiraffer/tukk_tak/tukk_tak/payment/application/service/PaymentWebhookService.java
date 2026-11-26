package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.command.TossWebhookCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.port.PaymentInquiryPort;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.PaymentWebhookResult;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.PgPaymentInfo;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.usecase.PaymentWebhookUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.Payment;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentMethod;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

/*
 * comment.
 *  결제 웹훅 서비스 (토스)
 *  웹훅 본문은 누구나 흉내 낼 수 있어서 믿지 않고, 결제키로 PG에 직접 조회한 상태만 믿는다.
 *  승인 성공은 승인 API가 처리하므로 여기서는 이후에 생긴 변화(수동 취소)만 반영한다.
 * */

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentWebhookService implements PaymentWebhookUsecase {

    private static final String EVENT_PAYMENT_STATUS_CHANGED = "PAYMENT_STATUS_CHANGED";
    private static final String STATUS_DONE = "DONE";
    private static final String STATUS_CANCELED = "CANCELED";

    // 서버 시간대와 상관없이 한국 시간 기준으로 취소 시각을 남긴다
    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private final PaymentInquiryPort paymentInquiryPort;
    private final PaymentRepository paymentRepository;
    private final PaymentCancelRegistrar paymentCancelRegistrar;

    @Override
    public PaymentWebhookResult handleTossWebhook(TossWebhookCommand command) {
        // 1. 결제 상태 변경 이벤트만 처리한다 (다른 이벤트나 결제키 없는 요청은 200으로 넘긴다)
        if (!EVENT_PAYMENT_STATUS_CHANGED.equals(command.eventType())
                || command.paymentKey() == null || command.paymentKey().isBlank()) {
            return PaymentWebhookResult.IGNORED;
        }

        // 2. 본문을 믿지 않고 PG에 결제 상태를 직접 조회한다 (통신이 실패하면 예외로 올라가 PG가 나중에 다시 보낸다)
        Optional<PgPaymentInfo> found = paymentInquiryPort.inquire(command.paymentKey());
        if (found.isEmpty()) {
            log.warn("[PAYMENT-WEBHOOK-UNKNOWN] PG에 없는 결제키 | eventType={}", command.eventType());
            return PaymentWebhookResult.IGNORED;
        }
        PgPaymentInfo info = found.get();

        // 3. PG가 알려준 상태로 나눠 처리한다
        return switch (info.status()) {
            case STATUS_CANCELED -> handleCanceled(info);
            case STATUS_DONE -> handleDone(info);
            default -> PaymentWebhookResult.IGNORED;
        };
    }

    // 취소된 결제: 우리 결제와 구독을 취소 상태로 바꾼다
    private PaymentWebhookResult handleCanceled(PgPaymentInfo info) {
        Optional<Payment> found = paymentRepository.findByOrderId(info.orderId());

        // 우리 DB의 토스 결제가 아니거나 결제키가 다르면 건드리지 않는다
        if (found.isEmpty() || found.get().getMethod() != PaymentMethod.TOSS
                || !found.get().getPgPaymentKey().equals(info.paymentKey())) {
            log.warn("[PAYMENT-WEBHOOK-NOT-OURS] 취소 알림이지만 일치하는 결제 없음 | orderId={}", info.orderId());
            return PaymentWebhookResult.IGNORED;
        }

        Payment payment = found.get();
        boolean canceled = paymentCancelRegistrar.cancel(payment, LocalDateTime.now(SEOUL));
        if (!canceled) {
            return PaymentWebhookResult.IGNORED;    // 이미 취소 반영됨 (재전송)
        }

        // 상위 플랜 구매로 뒤로 밀렸던 하위 플랜의 날짜는 되돌리지 않는다 (필요하면 이 로그로 수동 확인)
        log.warn("[PAYMENT-CANCELED-BY-WEBHOOK] memberId={}, orderId={}, subscriptionId={}",
                payment.getMemberId(), payment.getOrderId(), payment.getSubscriptionId());
        return PaymentWebhookResult.PROCESSED;
    }

    // 승인된 결제: 우리 DB에 이미 있으면 정상이다
    // 없으면 승인 API가 아직 저장 중일 수 있어서 나중에 다시 보내 달라고 하고, 계속 없으면 사람이 확인한다
    private PaymentWebhookResult handleDone(PgPaymentInfo info) {
        if (paymentRepository.existsByOrderId(info.orderId())) {
            return PaymentWebhookResult.IGNORED;
        }
        log.warn("[PAYMENT-WEBHOOK-NOT-RECORDED] 승인됐지만 우리 DB에 결제 없음 | orderId={}, amount={}",
                info.orderId(), info.totalAmount());
        return PaymentWebhookResult.RETRY;
    }

}
