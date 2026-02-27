package com.fiserv.uba.transaction.dto;

public record IdempotencyReplayResponse(String idempotencyKey,
                                        String operation,
                                        String transactionRef,
                                        String status,
                                        String responsePayload) {
}
