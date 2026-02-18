package com.fiserv.uba.gateway.service;

import com.fiserv.uba.gateway.dto.UpdatedUserContextDTO;
import com.fiserv.uba.gateway.exception.GatewayException;
import com.fiserv.uba.gateway.model.UserSessionContext;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TokenExchangeService implements TokenExchangeOperations {

    private final JwtUtil jwtUtil;
    private final RedisSessionOperations redisSessionService;

    public TokenExchangeService(JwtUtil jwtUtil, RedisSessionOperations redisSessionService) {
        this.jwtUtil = jwtUtil;
        this.redisSessionService = redisSessionService;
    }

    public Mono<String> exchange(UpdatedUserContextDTO ctx) {
        UserSessionContext session = new UserSessionContext(
                ctx.sub(), ctx.branchId(), ctx.drawerId(), ctx.itUserId(), ctx.roles(),
                List.of(), Map.of("cashWithdrawal", BigDecimal.valueOf(10000)), List.of());

        return redisSessionService.clearDrawerScoped(ctx.sub(), ctx.branchId(), ctx.drawerId())
                .then(redisSessionService.save(session, Duration.ofHours(4)))
                .flatMap(saved -> {
                    if (!saved) {
                        return Mono.error(new GatewayException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to persist session"));
                    }
                    return Mono.just(jwtUtil.createToken(ctx.sub(), ctx.branchId(), ctx.drawerId(), ctx.itUserId(), ctx.roles()));
                });
    }
}
