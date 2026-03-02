package com.fiserv.uba.transaction.client;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ComplianceDecisionClient {

    private final BigDecimal amlThreshold;
    private final List<String> ofacBlockedActors;
    private final RestClient auditRestClient;

    public ComplianceDecisionClient(@Value("${app.compliance.aml-threshold:10000}") BigDecimal amlThreshold,
                                    @Value("${app.compliance.ofac-blocked-actors:blocked-user}") List<String> ofacBlockedActors,
                                    @Value("${app.audit-service.base-url:http://localhost:8086}") String auditBaseUrl) {
        this.amlThreshold = amlThreshold;
        this.ofacBlockedActors = ofacBlockedActors;
        this.auditRestClient = RestClient.builder().baseUrl(auditBaseUrl).build();
    }

    public boolean isBlockedByOfac(String initiatedBy, BigDecimal amount) {
        boolean blocked = ofacBlockedActors.stream().anyMatch(a -> a.equalsIgnoreCase(initiatedBy));
        if (blocked) {
            createAlert("OFAC_BLOCK", initiatedBy, amount, "CRITICAL", "BLOCK");
        }
        return blocked;
    }

    public boolean isAmlThresholdBreached(BigDecimal amount) {
        boolean breached = amount != null && amount.compareTo(amlThreshold) >= 0;
        if (breached) {
            createAlert("AML_THRESHOLD_BREACH", "SYSTEM", amount, "HIGH", "NEEDS_REVIEW");
        }
        return breached;
    }

    private void createAlert(String eventType, String actor, BigDecimal amount, String severity, String ofacDecision) {
        try {
            auditRestClient.post()
                    .uri("/api/v1/compliance/alerts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new java.util.HashMap<String, Object>() {{
                        put("eventType", eventType);
                        put("branchId", "UNKNOWN");
                        put("drawerId", "UNKNOWN");
                        put("actor", actor);
                        put("severity", severity);
                        put("assignedTo", "compliance-queue");
                        put("slaHours", 4);
                        put("ofacDecision", ofacDecision);
                        put("overrideRationale", null);
                        put("amount", amount == null ? null : amount.toPlainString());
                    }})
                    .retrieve().toBodilessEntity();
        } catch (Exception ignored) {
            // non-blocking hook for local environments
        }
    }
}
