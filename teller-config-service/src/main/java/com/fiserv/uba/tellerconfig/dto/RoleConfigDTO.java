package com.fiserv.uba.tellerconfig.dto;

import java.util.List;

public record RoleConfigDTO(String roleId, List<String> permissions) {}
