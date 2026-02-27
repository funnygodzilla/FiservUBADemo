package com.fiserv.uba.gateway.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record UserSessionContext(
        String userId,
        String branchId,
        String drawerId,
        String itUserId,
        List<String> roles,
        List<String> permissions,
        Map<String, BigDecimal> limits,
        List<String> overrides
) {}
