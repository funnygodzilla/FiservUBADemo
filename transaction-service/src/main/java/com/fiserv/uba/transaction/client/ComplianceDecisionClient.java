package com.fiserv.uba.transaction.client;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class ComplianceDecisionClient {

    public boolean isBlockedByOfac(String initiatedBy, BigDecimal amount) {
        return false;
    }

    public boolean isAmlThresholdBreached(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.valueOf(10000)) > 0;
    }
}
