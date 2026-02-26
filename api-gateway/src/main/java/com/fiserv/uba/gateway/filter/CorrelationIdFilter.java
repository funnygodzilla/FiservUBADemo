package com.fiserv.uba.gateway.filter;

import java.util.UUID;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    public static final String CORRELATION_HEADER = "X-Correlation-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String incoming = exchange.getRequest().getHeaders().getFirst(CORRELATION_HEADER);
        final String correlationId = (incoming == null || incoming.isBlank()) ? UUID.randomUUID().toString() : incoming;

        ServerWebExchange updated = exchange.mutate()
                .request(builder -> builder.header(CORRELATION_HEADER, correlationId))
                .build();
        updated.getResponse().getHeaders().set(CORRELATION_HEADER, correlationId);
        updated.getAttributes().put(CORRELATION_HEADER, correlationId);
        return chain.filter(updated);
    }

    @Override
    public int getOrder() {
        return -300;
    }
}
