package com.fiserv.uba.tellerconfig.dto;

public record EntitlementDecisionDTO(boolean allowed,
                                     String actorRole,
                                     String reason,
                                     boolean approvalRequired) {
}
