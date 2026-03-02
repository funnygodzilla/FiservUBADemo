package com.fiserv.uba.transaction.dto;

import java.math.BigDecimal;

public record ReconcileRequest(String branchId, String drawerId, BigDecimal countedAmount) {}
