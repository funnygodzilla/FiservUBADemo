package com.fiserv.uba.gateway.filter;

import com.fiserv.uba.gateway.exception.GatewayException;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class DrawerContextEnrichmentFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (!"/cashbox/details".equals(path)) {
            return chain.filter(exchange);
        }

        Claims claims = (Claims) exchange.getAttribute("jwtClaims");
        if (claims == null) {
            return Mono.error(new GatewayException(HttpStatus.UNAUTHORIZED, "JWT claims not present"));
        }

        String branchId = claims.get("branchId", String.class);
        String drawerId = claims.get("drawerId", String.class);
        String itUserId = claims.get("itUserId", String.class);

        if (branchId == null || drawerId == null || itUserId == null) {
            return Mono.error(new GatewayException(HttpStatus.BAD_REQUEST, "JWT is not enriched with drawer context"));
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
