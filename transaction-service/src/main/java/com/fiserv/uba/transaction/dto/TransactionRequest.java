package com.fiserv.uba.transaction.dto;

import java.math.BigDecimal;

public record TransactionRequest(String branchId, String drawerId, String initiatedBy, BigDecimal amount, String reasonCode) {}
