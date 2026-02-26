package com.fiserv.uba.gateway.dto;

import java.math.BigDecimal;

public record CashBoxDTO(String drawerId, String branchId, BigDecimal availableCash) {}
