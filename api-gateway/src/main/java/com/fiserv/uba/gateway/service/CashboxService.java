package com.fiserv.uba.gateway.service;

import com.fiserv.uba.gateway.client.EsfClient;
import com.fiserv.uba.gateway.dto.CashBoxDTO;
import com.fiserv.uba.gateway.exception.GatewayException;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CashboxService {

    private final JwtUtil jwtUtil;
    private final RedisSessionOperations redisSessionService;
    private final EsfClient esfClient;

    public CashboxService(JwtUtil jwtUtil, RedisSessionOperations redisSessionService, EsfClient esfClient) {
        this.jwtUtil = jwtUtil;
        this.redisSessionService = redisSessionService;
        this.esfClient = esfClient;
    }

    public Mono<CashBoxDTO> getCashbox(String bearerToken) {
        Claims claims = jwtUtil.parse(stripBearer(bearerToken));
        String sub = claims.getSubject();
        String branchId = claims.get("branchId", String.class);
        String drawerId = claims.get("drawerId", String.class);

        if (branchId == null || drawerId == null) {
            return Mono.error(new GatewayException(HttpStatus.BAD_REQUEST, "JWT is not enriched with drawer context"));
        }

        return redisSessionService.get(sub, branchId, drawerId)
                .switchIfEmpty(Mono.error(new GatewayException(HttpStatus.UNAUTHORIZED, "Session not found in Redis")))
                .flatMap(session -> esfClient.getCashBoxDetails(bearerToken));
    }

    private String stripBearer(String bearer) {
        return bearer.replace("Bearer ", "").trim();
    }
}
