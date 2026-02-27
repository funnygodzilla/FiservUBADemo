package com.fiserv.uba.it.dto;

import java.math.BigDecimal;

public record CashBoxDTO(String drawerId, String branchId, BigDecimal availableCash) {}
