package com.fiserv.uba.transaction.service;

import com.fiserv.uba.transaction.client.AuditClient;
import com.fiserv.uba.transaction.domain.AuditOutboxEvent;
import com.fiserv.uba.transaction.repository.AuditOutboxEventRepository;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AuditOutboxPublisher {

    private final AuditOutboxEventRepository outboxRepository;
    private final AuditClient auditClient;

    public AuditOutboxPublisher(AuditOutboxEventRepository outboxRepository, AuditClient auditClient) {
        this.outboxRepository = outboxRepository;
        this.auditClient = auditClient;
    }

    @Scheduled(fixedDelayString = "${app.audit-outbox.flush-ms:5000}")
    public void flushPendingEvents() {
        List<AuditOutboxEvent> events = outboxRepository
                .findTop100ByStatusInAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(
                        List.of("PENDING", "RETRY"), OffsetDateTime.now());
        events.forEach(auditClient::flush);
    }
}
