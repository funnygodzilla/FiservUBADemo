package com.fiserv.uba.transaction.dto;

import java.math.BigDecimal;

public record AdjustmentRequest(String branchId,
                                String drawerId,
                                String initiatedBy,
                                BigDecimal amount,
                                String adjustmentType,
                                String reasonCode,
                                String terminalId,
                                String idempotencyKey) {
}
