package com.ohgiraffer.tukk_tak.tukk_tak.payment.infrastructure.adapter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.port.PaymentGatewayPort;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.application.result.KakaoReadyResult;
import com.ohgiraffer.tukk_tak.tukk_tak.payment.domain.exception.PaymentException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * comment.
 *  PaymentGatewayPort의 카카오페이 구현 (developers.kakaopay.com 단건결제 API)
 *  카카오 응답 형식(snake_case)과 에러를 이 클래스 안에서만 다루고, 서비스에는 KakaoReadyResult와 PaymentException만 내보낸다.
 *  외부 호출은 응답이 안 올 때를 대비해 연결/읽기 시간을 제한한다.
 * */

@Slf4j
@Component
public class KakaoPayGatewayAdapter implements PaymentGatewayPort {

    // 결제 준비(ready) API 주소
    private static final String READY_URL = "https://open-api.kakaopay.com/online/v1/payment/ready";
    // 연결 대기 시간과 응답 대기 시간 (결제 화면을 띄우기 전이라 길게 기다리지 않는다)
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(5);

    // 카카오 Secret key: 테스트는 Secret key(dev), 운영은 운영 키 (환경변수로 주입하고 코드에 쓰지 않는다)
    @Value("${payment.kakao.secret-key}") private String secretKey;
    // 가맹점 코드: 테스트 단건결제는 TC0ONETIME (키 환경과 반드시 맞춰야 한다)
    @Value("${payment.kakao.cid}") private String cid;
    // 결제 성공/취소/실패 후 돌아올 프론트 주소 (성공 주소로 pg_token이 붙어서 온다)
    @Value("${payment.kakao.approval-url}") private String approvalUrl;
    @Value("${payment.kakao.cancel-url}") private String cancelUrl;
    @Value("${payment.kakao.fail-url}") private String failUrl;

    private RestClient restClient;

    @PostConstruct
    void init() {
        // 서버 기동 시 한 번만 만들어 둔다 (요청마다 만들 필요 없음)
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECT_TIMEOUT);
        requestFactory.setReadTimeout(READ_TIMEOUT);
        this.restClient = RestClient.builder().requestFactory(requestFactory).build();
    }

    @Override
    public KakaoReadyResult readyKakao(String orderId, Long memberId, String orderName, int amount) {
        // 카카오 요청 본문 (필드명은 카카오 규격 그대로)
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("cid", cid);
        body.put("partner_order_id", orderId);
        body.put("partner_user_id", String.valueOf(memberId));
        body.put("item_name", orderName);
        body.put("quantity", 1);
        body.put("total_amount", amount);
        body.put("tax_free_amount", 0);
        body.put("approval_url", approvalUrl);
        body.put("cancel_url", cancelUrl);
        body.put("fail_url", failUrl);

        try {
            KakaoReadyResponse response = restClient.post()
                    .uri(READY_URL)
                    .header(HttpHeaders.AUTHORIZATION, "SECRET_KEY " + secretKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(KakaoReadyResponse.class);

            // 200인데 필요한 값이 없으면 비정상 응답으로 본다
            if (response == null || response.tid() == null || response.pcUrl() == null) {
                log.error("[kakao-ready] 비정상 응답 | orderId={}", orderId);
                throw PaymentException.gatewayUnavailable();
            }
            return new KakaoReadyResult(response.tid(), response.pcUrl(), response.mobileUrl());
        } catch (RestClientException e) {
            // 타임아웃, 연결 실패, 4xx/5xx 모두 같은 실패로 처리 (키/CID 불일치도 여기로 온다. 로그로 구분한다)
            log.error("[kakao-ready] 호출 실패 | orderId={} | reason={}", orderId, e.getMessage());
            throw PaymentException.gatewayUnavailable();
        }
    }

    // 카카오 ready 응답 중 필요한 값만 받는 내부 구조
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record KakaoReadyResponse(
            String tid,
            @JsonProperty("next_redirect_pc_url") String pcUrl,
            @JsonProperty("next_redirect_mobile_url") String mobileUrl
    ) {}


}
