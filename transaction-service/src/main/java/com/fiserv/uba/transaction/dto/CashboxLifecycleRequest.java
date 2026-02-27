package com.fiserv.uba.transaction.dto;

import java.math.BigDecimal;

public record CashboxLifecycleRequest(String branchId,
                                      String drawerId,
                                      String initiatedBy,
                                      BigDecimal amount,
                                      String terminalId,
                                      String idempotencyKey) {
}
