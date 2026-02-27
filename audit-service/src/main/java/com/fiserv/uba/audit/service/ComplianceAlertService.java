package com.fiserv.uba.audit.service;

import com.fiserv.uba.audit.domain.ComplianceAlert;
import com.fiserv.uba.audit.dto.ComplianceAlertRequest;
import com.fiserv.uba.audit.dto.ComplianceDispositionRequest;
import com.fiserv.uba.audit.dto.LegalHoldRequest;
import com.fiserv.uba.audit.repository.ComplianceAlertRepository;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ComplianceAlertService {
    private final ComplianceAlertRepository repository;

    public ComplianceAlertService(ComplianceAlertRepository repository) {
        this.repository = repository;
    }

    public ComplianceAlert create(ComplianceAlertRequest request) {
        ComplianceAlert alert = new ComplianceAlert();
        alert.setEventType(request.eventType());
        alert.setBranchId(request.branchId());
        alert.setDrawerId(request.drawerId());
        alert.setActor(request.actor());
        alert.setSeverity(request.severity());
        alert.setAssignedTo(request.assignedTo());
        alert.setStatus("OPEN");
        alert.setSlaDeadline(OffsetDateTime.now().plusHours(request.slaHours() == null ? 4 : request.slaHours()));
        alert.setOfacDecision(request.ofacDecision());
        if ("OVERRIDE".equalsIgnoreCase(request.ofacDecision())
                && (request.overrideRationale() == null || request.overrideRationale().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Override rationale is required");
        }
        alert.setOverrideRationale(request.overrideRationale());
        alert.setLegalHoldState("NONE");
        return repository.save(alert);
    }

    public ComplianceAlert disposition(Long alertId, ComplianceDispositionRequest request) {
        ComplianceAlert alert = repository.findById(alertId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found"));
        alert.setDisposition(request.disposition());
        alert.setStatus(request.status());
        alert.setAssignedTo(request.actor());
        return repository.save(alert);
    }

    public ComplianceAlert legalHold(Long alertId, LegalHoldRequest request) {
        ComplianceAlert alert = repository.findById(alertId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found"));
        alert.setLegalHoldState(request.holdState());
        return repository.save(alert);
    }

    public List<ComplianceAlert> report(String branchId, OffsetDateTime from, OffsetDateTime to) {
        return repository.findByBranchIdAndCreatedAtBetween(branchId, from, to);
    }
}
