package com.ohgiraffer.tukk_tak.tukk_tak.payment.application.service;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.command.ConfirmPaymentCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.command.PgApproveCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.port.PaymentApprovalPort;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.port.PaymentLockPort;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.port.PendingPaymentPort;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.ConfirmPaymentResult;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.PgApproval;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.usecase.PaymentConfirmUsecase;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.exception.PaymentException;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentMethod;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PendingPayment;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

/*
 * comment.
 *  결제 승인 서비스
 *  PG 호출은 트랜잭션 밖에서 하고, DB 반영만 PaymentRegistrar의 트랜잭션에 맡긴다.
 *  승인 뒤 DB 반영이 실패하면 PG에 보상 취소를 보내서 "돈만 나간 상태"가 남지 않게 한다.
 * */

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentConfirmService implements PaymentConfirmUsecase {

    // 한 번의 승인 처리가 끝나기에 충분한 시간 (PG 타임아웃 5초 + DB 반영)
    private static final Duration LOCK_TTL = Duration.ofSeconds(30);

    private final PaymentLockPort paymentLockPort;
    private final PendingPaymentPort pendingPaymentPort;
    private final PaymentRepository paymentRepository;
    private final PaymentRegistrar paymentRegistrar;
    private final List<PaymentApprovalPort> approvalPorts;

    @Override
    public ConfirmPaymentResult confirmPayment(ConfirmPaymentCommand command) {
        // 1. 회원 단위 락을 잡는다 (더블클릭이나 동시 요청으로 같은 주문이 두 번 승인되는 것을 막는다)
        String lockToken = paymentLockPort.tryLock(command.memberId(), LOCK_TTL)
                .orElseThrow(PaymentException::inProgress);

        try {
            return process(command);
        } finally {
            // 2. 락을 푼다 (내 토큰일 때만 지워서, ttl이 지난 뒤 다른 요청이 잡은 락을 건드리지 않는다)
            paymentLockPort.unlock(command.memberId(), lockToken);
        }
    }

    // 락을 잡은 상태에서 승인부터 반영까지 처리한다
    private ConfirmPaymentResult process(ConfirmPaymentCommand command) {
        // 1. 이미 처리된 주문인지 확인한다 (성공 뒤 같은 요청이 또 오면 임시 주문이 없어 "만료"로 보이므로 먼저 구분한다)
        if (paymentRepository.existsByOrderId(command.orderId())) {
            throw PaymentException.alreadyProcessed();
        }

        // 2. 서버가 저장해 둔 진행 중 주문을 꺼낸다 (플랜, 개월, 금액은 클라이언트가 아니라 이 값만 믿는다)
        PendingPayment pending = pendingPaymentPort.find(command.memberId())
                .orElseThrow(PaymentException::pendingNotFound);

        // 3. 요청이 주문과 같은지 대조한다 (다른 주문의 승인 정보를 끼워 넣거나 금액을 바꾸는 위조를 막는다)
        boolean tossAmountDiffers = command.method() == PaymentMethod.TOSS
                && pending.amount() != command.amount();
        if (!pending.orderId().equals(command.orderId()) || pending.method() != command.method()
                || tossAmountDiffers) {
            throw PaymentException.orderMismatch();
        }

        // 4. PG에 승인을 요청한다 (트랜잭션 밖에서 호출해 외부 호출 동안 DB 커넥션을 잡지 않는다)
        PaymentApprovalPort approvalPort = portOf(pending.method());
        PgApproval approval = approvalPort.approve(new PgApproveCommand(pending.orderId(),
                command.memberId(), pending.amount(), command.paymentKey(), pending.kakaoTid(),
                command.pgToken()));

        // 5. PG가 승인한 금액을 다시 확인한다 (다르면 승인된 돈을 바로 되돌린다)
        if (approval.amount() != pending.amount()) {
            compensate(approvalPort, approval, pending, command.memberId(), "승인 금액 불일치");
            throw PaymentException.orderMismatch();
        }

        // 6. DB에 반영한다 (구독 재배치 + 구독 저장 + 결제 저장은 한 트랜잭션, 실패하면 승인을 취소한다)
        ConfirmPaymentResult result;
        try {
            result = paymentRegistrar.register(command.memberId(), pending, approval);
        } catch (RuntimeException e) {
            log.error("[PAYMENT-ACTIVATION-FAILED] memberId={}, orderId={}", command.memberId(),
                    pending.orderId(), e);
            compensate(approvalPort, approval, pending, command.memberId(), "이용권 적용 실패");
            throw PaymentException.activationFailed();
        }

        // 7. 임시 주문을 지운다 (성공한 주문이 남아 있으면 같은 주문으로 재승인을 시도할 수 있다)
        pendingPaymentPort.delete(command.memberId());

        return result;
    }

    // 결제수단에 맞는 PG 구현체를 고른다
    private PaymentApprovalPort portOf(PaymentMethod method) {
        return approvalPorts.stream()
                .filter(port -> port.method() == method)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("PG 구현체가 없는 결제수단: " + method));
    }

    // 승인된 결제를 취소한다 (취소까지 실패하면 돈은 나갔는데 기록이 없는 상태라, 수동 처리에 필요한 값을 ERROR로 남긴다)
    private void compensate(PaymentApprovalPort port, PgApproval approval, PendingPayment pending,
                            Long memberId, String reason) {
        try {
            port.cancel(approval.paymentKey(), approval.amount(), reason);
        } catch (RuntimeException e) {
            log.error("[PAYMENT-COMPENSATION-FAILED] memberId={}, orderId={}, method={}, paymentKey={}, amount={}",
                    memberId, pending.orderId(), pending.method(), approval.paymentKey(),
                    approval.amount(), e);
        }
    }

}
