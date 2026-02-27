package com.fiserv.uba.audit.dto;

public record ComplianceAlertRequest(String eventType,
                                     String branchId,
                                     String drawerId,
                                     String actor,
                                     String severity,
                                     String assignedTo,
                                     Integer slaHours,
                                     String ofacDecision,
                                     String overrideRationale) {
}
