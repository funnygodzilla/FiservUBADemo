package com.fiserv.uba.transaction.dto;

import java.math.BigDecimal;

public record TransactionResponse(String txnRef, String status, BigDecimal postBalance) {}
