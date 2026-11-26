package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.Payment;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.repository.PaymentRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.subscription.domain.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/*
 * comment.
 *  결제 취소를 DB에 반영하는 단계 (결제 취소 + 구독 취소를 한 트랜잭션으로 묶는다)
 *  PG 조회는 트랜잭션 밖의 서비스가 하고, 여기서는 DB 반영만 한다.
 * */

@Component
@RequiredArgsConstructor
public class PaymentCancelRegistrar {

    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;

    // 이미 취소된 결제면 아무것도 바꾸지 않고 false (같은 웹훅이 여러 번 와도 한 번만 반영된다)
    @Transactional
    public boolean cancel(Payment payment, LocalDateTime canceledAt) {
        if (!paymentRepository.markCanceled(payment.getId(), canceledAt)) {
            return false;
        }
        subscriptionRepository.cancel(payment.getSubscriptionId(), canceledAt);
        return true;
    }

}
