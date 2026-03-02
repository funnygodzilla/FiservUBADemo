package com.fiserv.uba.audit.repository;

import com.fiserv.uba.audit.domain.AuditEvent;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {
    List<AuditEvent> findByCreatedAtBetween(OffsetDateTime from, OffsetDateTime to);
}
