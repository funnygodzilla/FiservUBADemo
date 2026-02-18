package com.fiserv.uba.gateway.dto;

import java.util.List;

public record UpdatedUserContextDTO(String sub, String branchId, String drawerId, String itUserId, List<String> roles) {}
