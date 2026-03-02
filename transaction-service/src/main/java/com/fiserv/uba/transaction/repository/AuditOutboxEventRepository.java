package com.fiserv.uba.transaction.repository;

import com.fiserv.uba.transaction.domain.AuditOutboxEvent;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditOutboxEventRepository extends JpaRepository<AuditOutboxEvent, Long> {
    List<AuditOutboxEvent> findTop100ByStatusInAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(
            List<String> status, OffsetDateTime nextAttemptAt);
}
