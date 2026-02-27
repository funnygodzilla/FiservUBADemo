package com.fiserv.uba.user.dto;

import java.util.List;

public record UpdatedUserContextDTO(String sub, String branchId, String drawerId, String itUserId, List<String> roles) {}
