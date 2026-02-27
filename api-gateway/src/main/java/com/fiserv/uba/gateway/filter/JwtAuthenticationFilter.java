package com.fiserv.uba.gateway.filter;

import com.fiserv.uba.gateway.exception.GatewayException;
import com.fiserv.uba.gateway.service.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.startsWith("/actuator")) {
            return chain.filter(exchange);
        }

        String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            return Mono.error(new GatewayException(HttpStatus.UNAUTHORIZED, "Missing bearer token"));
        }

        try {
            Claims claims = jwtUtil.parse(auth.substring(7).trim());
            exchange.getAttributes().put("jwtClaims", claims);
            return chain.filter(exchange);
        } catch (Exception ex) {
            return Mono.error(new GatewayException(HttpStatus.UNAUTHORIZED, "Invalid JWT"));
        }
    }

    @Override
    public int getOrder() {
        return -200;
    }
}
