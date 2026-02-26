package com.fiserv.uba.transaction.dto;

import java.math.BigDecimal;

public record ReconcileResponse(String drawerId, BigDecimal expectedAmount, BigDecimal countedAmount, BigDecimal variance) {}
