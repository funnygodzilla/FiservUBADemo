package com.fiserv.uba.esf.dto;

import java.math.BigDecimal;

public record CashBoxDTO(String drawerId, String branchId, BigDecimal availableCash) {}
