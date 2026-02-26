package com.fiserv.uba.audit.dto;

public record AuditEventRequest(
        String actor,
        String branchId,
        String drawerId,
        String correlationId,
        String action,
        String beforeState,
        String afterState
) {}
