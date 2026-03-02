package com.fiserv.uba.transaction.dto;

import java.math.BigDecimal;

public record VarianceApprovalRequest(String branchId,
                                      String drawerId,
                                      String initiatedBy,
                                      String approverId,
                                      String approverRole,
                                      BigDecimal countedAmount,
                                      String reasonCode) {
}
