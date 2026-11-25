package com.ohgiraffer.tukk_tak.tukk_tak.member.infrastructure.adapter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ohgiraffer.tukk_tak.tukk_tak.member.application.port.BusinessVerifier;
import com.ohgiraffer.tukk_tak.tukk_tak.member.application.result.BusinessCheckResult;
import com.ohgiraffer.tukk_tak.tukk_tak.member.domain.exception.MemberException;
import com.ohgiraffer.tukk_tak.tukk_tak.member.infrastructure.config.NtsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/*
 * comment.
 *  국세청 사업자등록정보 진위확인 API 구현체 (공공데이터포털)
 *  -
 *  1) /validate : 사업자번호 + 개업일자 + 대표자명이 일치하는지 (valid 01 일치, 02 불일치)
 *  2) 일치하는데 응답에 사업자 상태(b_stt_cd)가 없으면 /status 를 한 번 더 호출한다. (01 계속, 02 휴업, 03 폐업)
 *  -
 *  타임아웃(연결 3초, 응답 5초)을 반드시 걸어서, 국세청이 느려도 우리 서버 스레드가 묶이지 않게 한다.
 *  호출 실패, 타임아웃, 4xx/5xx, 이상한 응답은 모두 503(AUTH-BUSINESS-VERIFY-UNAVAILABLE)로 통일한다.
 *  (4xx는 대개 우리 쪽 키 설정 문제라서 로그에 error로 남긴다)
 *  -
 *  @Component를 붙이지 않았다. 어떤 구현체를 쓸지는 BusinessVerifierConfig가 정한다.
 * */

@Slf4j
public class NtsBusinessVerifier implements BusinessVerifier {

    private static final String VALID_MATCHED = "01";   // 진위확인 결과: 일치
    private static final String STATUS_CLOSED = "03";   // 사업자 상태: 폐업 (02 휴업은 허용)

    private final RestClient restClient;
    private final String serviceKey;

    /*
     * comment.
     *  RestClient를 만들면서 타임아웃을 건다.
     *  타임아웃을 안 걸면 국세청이 응답하지 않을 때 요청 스레드가 무한정 기다리게 된다.
     * */
    public NtsBusinessVerifier(NtsProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout((int) properties.connectTimeout().toMillis());
        requestFactory.setReadTimeout((int) properties.readTimeout().toMillis());

        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactory)
                .build();
        this.serviceKey = properties.serviceKey();
    }

    /*
     * comment.
     *  진위확인 -> (일치하면) 사업자 상태 확인 순서로 판정한다.
     *  폐업(03)만 closed=true 이고, 휴업(02)과 계속사업자(01)는 통과시킨다.
     * */
    @Override
    public BusinessCheckResult verify(String businessNumber, LocalDate openDate, String ceoName) {
        try {
            // 1. 진위확인 호출 (국세청에는 개업일자를 yyyyMMdd로 보낸다)
            ValidateResponse response = restClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/validate")
                            .queryParam("serviceKey", "{key}")   // 템플릿으로 넣어야 Decoding 키가 올바르게 인코딩된다
                            .build(serviceKey))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ValidateRequest(List.of(new Business(
                            businessNumber, openDate.format(DateTimeFormatter.BASIC_ISO_DATE), ceoName))))
                    .retrieve()
                    .body(ValidateResponse.class);

            ValidateItem item = firstItem(response);

            // 2. 일치하지 않으면 상태는 볼 필요가 없다
            if (!VALID_MATCHED.equals(item.valid())) {
                return BusinessCheckResult.mismatch();
            }

            // 3. 일치하면 상태 확인: 응답에 상태가 있으면 그대로 쓰고, 없으면 상태조회 API를 한 번 더 호출
            String statusCode = (item.status() != null) ? item.status().statusCode() : null;
            if (!StringUtils.hasText(statusCode)) {
                statusCode = fetchStatusCode(businessNumber);
            }

            // 4. 일치(valid=01)인데 상태를 끝내 알 수 없으면, 폐업자가 통과하는 쪽보다 503이 안전하다
            if (!StringUtils.hasText(statusCode)) {
                log.error("[nts] 일치인데 사업자 상태를 확인하지 못함");
                throw MemberException.businessVerifyUnavailable();
            }

            // 5. 폐업(03)만 거절하고 휴업(02)은 허용한다
            return BusinessCheckResult.matched(STATUS_CLOSED.equals(statusCode));

        } catch (RestClientException e) {
            // 연결 실패, 타임아웃, 4xx, 5xx, 응답 변환 실패가 모두 여기로 온다
            // 인증키가 담긴 요청 주소는 로그에 남기지 않고 예외 종류만 남긴다
            log.error("[nts] 국세청 호출 실패 | type={}", e.getClass().getSimpleName());
            throw MemberException.businessVerifyUnavailable();
        }
    }

    /*
     * comment.
     *  상태조회 API: 사업자번호 하나의 상태 코드(01 계속, 02 휴업, 03 폐업)를 돌려준다.
     *  진위확인 응답에 상태가 없을 때만 호출되며, 응답이 비어 있으면 null을 돌려준다.
     * */
    private String fetchStatusCode(String businessNumber) {
        StatusResponse response = restClient.post()
                .uri(uriBuilder -> uriBuilder.path("/status")
                        .queryParam("serviceKey", "{key}")
                        .build(serviceKey))
                .contentType(MediaType.APPLICATION_JSON)
                .body(new StatusRequest(List.of(businessNumber)))
                .retrieve()
                .body(StatusResponse.class);

        // 응답이 비어 있으면 상태를 알 수 없다
        if (response == null || response.data() == null || response.data().isEmpty()) {
            return null;
        }
        return response.data().get(0).statusCode();
    }

    /*
     * comment.
     *  진위확인 응답에서 첫 번째 결과를 꺼낸다.
     *  요청은 사업자번호 하나뿐이므로 결과도 하나여야 하고, 비어 있으면 비정상 응답으로 보고 503으로 처리한다.
     * */
    private ValidateItem firstItem(ValidateResponse response) {
        if (response == null || response.data() == null || response.data().isEmpty()) {
            log.error("[nts] 비정상 응답: data 없음");
            throw MemberException.businessVerifyUnavailable();
        }
        return response.data().get(0);
    }

    // ---- 국세청 요청/응답 모양 (이 클래스 안에서만 쓴다) ----

    // 진위확인 요청의 사업자 한 건 (국세청이 정한 필드 이름: b_no, start_dt, p_nm)
    private record Business(
            @JsonProperty("b_no") String businessNumber,
            @JsonProperty("start_dt") String openDate,
            @JsonProperty("p_nm") String ceoName) {
    }

    // 진위확인 요청 본문
    private record ValidateRequest(List<Business> businesses) {
    }

    // 상태조회 요청 본문
    private record StatusRequest(@JsonProperty("b_no") List<String> businessNumbers) {
    }

    // 진위확인 응답 (필요한 data만 읽고 나머지 필드는 무시)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ValidateResponse(List<ValidateItem> data) {
    }

    // 진위확인 결과 한 건: valid(01 일치, 02 불일치), status(없을 수도 있음)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ValidateItem(String valid, StatusItem status) {
    }

    // 사업자 상태 (b_stt_cd: 01 계속, 02 휴업, 03 폐업)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record StatusItem(@JsonProperty("b_stt_cd") String statusCode) {
    }

    // 상태조회 응답
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record StatusResponse(List<StatusItem> data) {
    }

}
