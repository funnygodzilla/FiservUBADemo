package com.fiserv.uba.transaction.client;

import com.fiserv.uba.transaction.domain.AuditOutboxEvent;
import com.fiserv.uba.transaction.repository.AuditOutboxEventRepository;
import java.time.OffsetDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AuditClient {

    private final RestClient restClient;
    private final AuditOutboxEventRepository outboxRepository;

    public AuditClient(@Value("${app.audit-service.base-url:http://localhost:8086}") String baseUrl,
                       AuditOutboxEventRepository outboxRepository) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.outboxRepository = outboxRepository;
    }

    public void publish(String action, String actor, String branchId, String drawerId,
                        String correlationId, String beforeState, String afterState) {
        AuditOutboxEvent event = new AuditOutboxEvent();
        event.setAction(action);
        event.setActor(actor);
        event.setBranchId(branchId);
        event.setDrawerId(drawerId);
        event.setCorrelationId(correlationId);
        event.setBeforeState(beforeState);
        event.setAfterState(afterState);
        event.setStatus("PENDING");
        event.setNextAttemptAt(OffsetDateTime.now());
        outboxRepository.save(event);
        flush(event);
    }

    public void flush(AuditOutboxEvent event) {
        try {
            restClient.post()
                    .uri("/api/v1/audit-events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "actor", event.getActor(),
                            "branchId", event.getBranchId(),
                            "drawerId", event.getDrawerId(),
                            "correlationId", event.getCorrelationId(),
                            "action", event.getAction(),
                            "beforeState", event.getBeforeState(),
                            "afterState", event.getAfterState()
                    ))
                    .retrieve()
                    .toBodilessEntity();
            event.setStatus("PUBLISHED");
            event.setLastError(null);
        } catch (Exception ex) {
            event.setStatus("RETRY");
            event.setRetryCount(event.getRetryCount() + 1);
            event.setLastError(ex.getMessage());
            event.setNextAttemptAt(OffsetDateTime.now().plusSeconds(Math.min(300, event.getRetryCount() * 15L)));
        }
        outboxRepository.save(event);
    }
}
