package com.fiserv.uba.tellerconfig.dto;

import java.math.BigDecimal;

public record EntitlementRequestDTO(String actorId,
                                    String operation,
                                    String branchId,
                                    String drawerId,
                                    BigDecimal amount) {
}
