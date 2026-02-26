package com.fiserv.uba.transaction.client;

import org.springframework.stereotype.Component;

@Component
public class AuditClient {
    public void publish(String action, String actor, String branchId, String drawerId, String correlationId, String beforeState, String afterState) {
        // placeholder for audit-service integration
    }
}
