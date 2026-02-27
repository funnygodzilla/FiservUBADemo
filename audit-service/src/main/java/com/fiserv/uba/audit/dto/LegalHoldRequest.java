package com.fiserv.uba.audit.dto;

public record LegalHoldRequest(String holdState,
                               String actor,
                               String reason) {
}
