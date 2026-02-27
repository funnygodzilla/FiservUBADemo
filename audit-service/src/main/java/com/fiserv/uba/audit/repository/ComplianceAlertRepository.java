package com.fiserv.uba.audit.repository;

import com.fiserv.uba.audit.domain.ComplianceAlert;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplianceAlertRepository extends JpaRepository<ComplianceAlert, Long> {
    List<ComplianceAlert> findByBranchIdAndCreatedAtBetween(String branchId, OffsetDateTime from, OffsetDateTime to);
}
