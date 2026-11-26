package com.ohgiraffer.tukk_tak.tukk_tak.payment.infrastructure.adapter;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.Payment;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.repository.PaymentRepository;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.infrastructure.persistence.PaymentJpaEntity;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.infrastructure.persistence.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/*
 * comment.
 *  PaymentRepository 포트의 구현체
 *  저장은 호출하는 PaymentRegistrar가 트랜잭션을 시작한다.
 * */

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;

    @Override
    public Payment save(Payment payment) {
        return jpaRepository.save(PaymentJpaEntity.fromDomain(payment)).toDomain();
    }

    @Override
    public boolean existsByOrderId(String orderId) {
        return jpaRepository.existsByOrderId(orderId);
    }

}
