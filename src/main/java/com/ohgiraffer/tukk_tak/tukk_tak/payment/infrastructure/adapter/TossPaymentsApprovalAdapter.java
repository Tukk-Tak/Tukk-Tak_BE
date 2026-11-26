package com.ohgiraffer.tukk_tak.tukk_tak.payment.infrastructure.adapter;

import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.command.PgApproveCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.port.PaymentApprovalPort;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.port.PaymentInquiryPort;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.PgApproval;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.PgPaymentInfo;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.exception.PaymentException;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.model.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

/*
 * comment.
 *  토스페이먼츠 승인/취소/조회 어댑터
 *  시크릿 키를 Basic 인증으로 보내고, 승인 요청에는 주문번호를 Idempotency-Key로 붙인다.
 *  응답이 오기 전에 타임아웃이 나도 같은 주문으로 다시 승인하면 토스가 처음 결과를 돌려준다.
 *  그래서 승인 여부 조회(findApproved)는 따로 구현하지 않고 포트의 기본 구현(조회 없음)을 쓴다.
 *  결제 조회(inquire)는 웹훅을 믿지 않고 토스에 직접 상태를 확인할 때 쓴다.
 * */

@Slf4j
@Component
public class TossPaymentsApprovalAdapter implements PaymentApprovalPort, PaymentInquiryPort {

    private static final String CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private static final String CANCEL_URL = "https://api.tosspayments.com/v1/payments/%s/cancel";
    private static final String PAYMENT_URL = "https://api.tosspayments.com/v1/payments/{paymentKey}";
    private static final String DONE = "DONE";

    private final RestClient restClient;
    private final String authorization;

    public TossPaymentsApprovalAdapter(@Value("${payment.toss.secret-key}") String secretKey) {
        // 외부 서버가 멈춰도 요청 스레드가 오래 붙잡히지 않게 타임아웃을 둔다
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(5));
        this.restClient = RestClient.builder().requestFactory(factory).build();
        // 토스는 "시크릿키:" 를 Base64로 인코딩해서 Basic 인증으로 받는다 (비밀번호 자리는 비운다)
        this.authorization = "Basic " + Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public PaymentMethod method() {
        return PaymentMethod.TOSS;
    }

    @Override
    public PgApproval approve(PgApproveCommand command) {
        try {
            TossConfirmResponse response = restClient.post()
                    .uri(CONFIRM_URL)
                    .header(HttpHeaders.AUTHORIZATION, authorization)
                    .header("Idempotency-Key", command.orderId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("paymentKey", command.paymentKey(), "orderId", command.orderId(),
                            "amount", command.amount()))
                    .retrieve()
                    .body(TossConfirmResponse.class);

            if (response == null || !DONE.equals(response.status())) {
                throw PaymentException.gatewayRejected();
            }
            return new PgApproval(response.paymentKey(), response.totalAmount());
        } catch (HttpClientErrorException e) {
            // 4xx는 PG가 거절한 것이다. 원본 코드와 문구는 로그에만 남기고 사용자에게는 통일된 문구를 준다
            log.warn("[TOSS-CONFIRM-REJECTED] orderId={}, body={}", command.orderId(), e.getResponseBodyAsString());
            throw PaymentException.gatewayRejected();
        } catch (RestClientException e) {
            // 5xx, 타임아웃은 승인 여부를 알 수 없다. 같은 주문으로 다시 시도하면 Idempotency-Key가 처음 결과를 돌려준다
            log.warn("[TOSS-CONFIRM-UNAVAILABLE] orderId={}", command.orderId(), e);
            throw PaymentException.gatewayUnavailable();
        }
    }

    @Override
    public void cancel(String paymentKey, int amount, String reason) {
        restClient.post()
                .uri(CANCEL_URL.formatted(paymentKey))
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .header("Idempotency-Key", "cancel-" + paymentKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("cancelReason", reason, "cancelAmount", amount))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public Optional<PgPaymentInfo> inquire(String paymentKey) {
        try {
            // 결제키는 웹훅 본문에서 온 믿을 수 없는 값이라, 문자열을 이어 붙이지 않고 URI 변수로 넘겨서 인코딩되게 한다
            TossPaymentResponse response = restClient.get()
                    .uri(PAYMENT_URL, paymentKey)
                    .header(HttpHeaders.AUTHORIZATION, authorization)
                    .retrieve()
                    .body(TossPaymentResponse.class);

            if (response == null) {
                return Optional.empty();
            }
            return Optional.of(new PgPaymentInfo(response.paymentKey(), response.orderId(),
                    response.status(), response.totalAmount()));
        } catch (HttpClientErrorException.NotFound e) {
            // 토스에 없는 결제키 (가짜 웹훅이거나 다른 상점의 결제)
            return Optional.empty();
        } catch (HttpClientErrorException e) {
            // 인증 오류 등 우리 설정 문제일 수 있어서 ERROR로 남기고, 응답이 200이 아니게 해서 나중에 다시 받는다
            log.error("[TOSS-INQUIRE-REJECTED] body={}", e.getResponseBodyAsString());
            throw PaymentException.gatewayUnavailable();
        } catch (RestClientException e) {
            log.warn("[TOSS-INQUIRE-UNAVAILABLE]", e);
            throw PaymentException.gatewayUnavailable();
        }
    }

    // 토스 승인 응답에서 쓰는 값만 받는다 (나머지는 무시)
    private record TossConfirmResponse(String paymentKey, String status, int totalAmount) {
    }

    // 토스 결제 조회 응답에서 쓰는 값만 받는다 (나머지는 무시)
    private record TossPaymentResponse(String paymentKey, String orderId, String status, int totalAmount) {
    }

}
