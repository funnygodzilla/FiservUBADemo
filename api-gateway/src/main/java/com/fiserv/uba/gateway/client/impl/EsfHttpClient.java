package com.fiserv.uba.gateway.client.impl;

import com.fiserv.uba.gateway.client.EsfClient;
import com.fiserv.uba.gateway.dto.CashBoxDTO;
import com.fiserv.uba.gateway.exception.GatewayException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class EsfHttpClient implements EsfClient {

    private final WebClient webClient;

    public EsfHttpClient(@Qualifier("esfWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<CashBoxDTO> getCashBoxDetails(String bearerToken) {
        return webClient.get()
                .uri("/cashbox/details")
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .onStatus(status -> status.isError(), r -> r.bodyToMono(String.class)
                        .defaultIfEmpty("esf unavailable")
                        .flatMap(m -> Mono.error(new GatewayException(HttpStatus.BAD_GATEWAY, m))))
                .bodyToMono(CashBoxDTO.class);
    }
}
