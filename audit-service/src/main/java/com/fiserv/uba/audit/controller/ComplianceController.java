package com.fiserv.uba.audit.controller;

import com.fiserv.uba.audit.domain.ComplianceAlert;
import com.fiserv.uba.audit.dto.ComplianceAlertRequest;
import com.fiserv.uba.audit.dto.ComplianceDispositionRequest;
import com.fiserv.uba.audit.dto.LegalHoldRequest;
import com.fiserv.uba.audit.service.ComplianceAlertService;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/compliance")
public class ComplianceController {

    private final ComplianceAlertService service;

    public ComplianceController(ComplianceAlertService service) {
        this.service = service;
    }

    @PostMapping("/alerts")
    public ComplianceAlert create(@RequestBody ComplianceAlertRequest request) {
        return service.create(request);
    }

    @PostMapping("/alerts/{alertId}/disposition")
    public ComplianceAlert disposition(@PathVariable Long alertId, @RequestBody ComplianceDispositionRequest request) {
        return service.disposition(alertId, request);
    }

    @PostMapping("/alerts/{alertId}/legal-hold")
    public ComplianceAlert legalHold(@PathVariable Long alertId, @RequestBody LegalHoldRequest request) {
        return service.legalHold(alertId, request);
    }

    @GetMapping("/reports")
    public List<ComplianceAlert> report(@RequestParam String branchId,
                                        @RequestParam OffsetDateTime from,
                                        @RequestParam OffsetDateTime to) {
        return service.report(branchId, from, to);
    }
}
