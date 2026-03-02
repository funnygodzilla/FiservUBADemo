package com.fiserv.uba.transaction.dto;

public record EntitlementDecision(boolean allowed,
                                  String actorRole,
                                  String reason,
                                  boolean approvalRequired) {
}
