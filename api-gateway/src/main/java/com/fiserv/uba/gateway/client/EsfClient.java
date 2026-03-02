package com.fiserv.uba.gateway.client;

import com.fiserv.uba.gateway.dto.CashBoxDTO;
import reactor.core.publisher.Mono;

public interface EsfClient {
    Mono<CashBoxDTO> getCashBoxDetails(String bearerToken, String correlationId);
}
