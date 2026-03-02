package com.fiserv.uba.transaction.client;

import com.fiserv.uba.transaction.dto.EntitlementDecision;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class EntitlementClient {

    private final RestClient restClient;

    public EntitlementClient(@Value("${app.teller-config.base-url:http://localhost:8083}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public EntitlementDecision check(String actorId, String operation, String branchId, String drawerId, BigDecimal amount) {
        return restClient.post()
                .uri("/config/entitlements/check")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "actorId", actorId,
                        "operation", operation,
                        "branchId", branchId,
                        "drawerId", drawerId,
                        "amount", amount
                ))
                .retrieve()
                .body(EntitlementDecision.class);
    }
}
