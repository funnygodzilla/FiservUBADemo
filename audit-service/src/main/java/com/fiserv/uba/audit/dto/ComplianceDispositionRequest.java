package com.fiserv.uba.audit.dto;

public record ComplianceDispositionRequest(String disposition,
                                           String status,
                                           String actor) {
}
