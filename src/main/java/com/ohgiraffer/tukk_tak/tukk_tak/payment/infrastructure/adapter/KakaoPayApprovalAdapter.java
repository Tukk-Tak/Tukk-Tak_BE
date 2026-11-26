package com.ohgiraffer.tukk_tak.tukk_tak.payment.infrastructure.adapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.command.PgApproveCommand;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.port.PaymentApprovalPort;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.PgApproval;
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

import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
public class KakaoPayApprovalAdapter implements PaymentApprovalPort {

    private static final String APPROVE_URL = "https://open-api.kakaopay.com/online/v1/payment/approve";
    private static final String CANCEL_URL = "https://open-api.kakaopay.com/online/v1/payment/cancel";

    private final RestClient restClient;
    private final String authorization;
    private final String cid;

    public KakaoPayApprovalAdapter(@Value("${payment.kakao.secret-key}") String secretKey,
                                   @Value("${payment.kakao.cid}") String cid) {
        // 외부 서버가 멈춰도 요청 스레드가 오래 붙잡히지 않게 타임아웃을 둔다
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(5));
        this.restClient = RestClient.builder().requestFactory(factory).build();
        this.authorization = "SECRET_KEY " + secretKey;
        this.cid = cid;
    }

    @Override
    public PaymentMethod method() {
        return PaymentMethod.KAKAOPAY;
    }

    @Override
    public PgApproval approve(PgApproveCommand command) {
        try {
            KakaoApproveResponse response = restClient.post()
                    .uri(APPROVE_URL)
                    .header(HttpHeaders.AUTHORIZATION, authorization)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("cid", cid, "tid", command.kakaoTid(),
                            "partner_order_id", command.orderId(),
                            "partner_user_id", String.valueOf(command.memberId()),
                            "pg_token", command.pgToken()))
                    .retrieve()
                    .body(KakaoApproveResponse.class);

            if (response == null || response.amount() == null) {
                throw PaymentException.gatewayRejected();
            }
            return new PgApproval(response.tid(), response.amount().total());
        } catch (HttpClientErrorException e) {
            // 4xx는 PG가 거절한 것이다. 원본 코드와 문구는 로그에만 남기고 사용자에게는 통일된 문구를 준다
            log.warn("[KAKAO-APPROVE-REJECTED] orderId={}, body={}", command.orderId(), e.getResponseBodyAsString());
            throw PaymentException.gatewayRejected();
        } catch (RestClientException e) {
            // 5xx, 타임아웃은 승인 여부를 알 수 없어서 로그에 주문번호를 남겨 대조할 수 있게 한다
            log.warn("[KAKAO-APPROVE-UNAVAILABLE] orderId={}", command.orderId(), e);
            throw PaymentException.gatewayUnavailable();
        }
    }

    @Override
    public void cancel(String paymentKey, int amount, String reason) {
        restClient.post()
                .uri(CANCEL_URL)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("cid", cid, "tid", paymentKey, "cancel_amount", amount,
                        "cancel_tax_free_amount", 0))
                .retrieve()
                .toBodilessEntity();
    }

    // 카카오 승인 응답에서 쓰는 값만 받는다 (나머지는 무시)
    private record KakaoApproveResponse(String tid, Amount amount) {
    }

    private record Amount(@JsonProperty("total") int total) {
    }

}
