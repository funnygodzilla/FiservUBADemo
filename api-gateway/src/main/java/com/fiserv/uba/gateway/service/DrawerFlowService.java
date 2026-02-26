package com.fiserv.uba.gateway.service;

import com.fiserv.uba.gateway.client.UserManagementClient;
import com.fiserv.uba.gateway.dto.DrawerDTO;
import com.fiserv.uba.gateway.dto.UpdatedUserContextDTO;
import com.fiserv.uba.gateway.exception.GatewayException;
import io.jsonwebtoken.Claims;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DrawerFlowService {

    private final JwtUtil jwtUtil;
    private final UserManagementClient userManagementClient;
    private final TokenExchangeOperations tokenExchangeService;

    public DrawerFlowService(JwtUtil jwtUtil, UserManagementClient userManagementClient, TokenExchangeOperations tokenExchangeService) {
        this.jwtUtil = jwtUtil;
        this.userManagementClient = userManagementClient;
        this.tokenExchangeService = tokenExchangeService;
    }

    public Mono<DrawerFetchResult> fetchDrawers(String bearerToken) {
        Claims claims = jwtUtil.parse(stripBearer(bearerToken));
        String sub = claims.getSubject();
        return userManagementClient.getDrawers(sub)
                .flatMap(drawers -> {
                    if (drawers.size() == 1) {
                        String drawerId = drawers.get(0).drawerId();
                        return userManagementClient.selectDrawer(sub, drawerId)
                                .flatMap(tokenExchangeService::exchange)
                                .map(jwt -> new DrawerFetchResult(drawers, jwt));
                    }
                    return Mono.just(new DrawerFetchResult(drawers, null));
                })
                .onErrorMap(e -> new GatewayException(HttpStatus.BAD_REQUEST, "Unable to fetch/select drawer: " + e.getMessage()));
    }

    public Mono<String> selectDrawer(String bearerToken, String drawerId) {
        Claims claims = jwtUtil.parse(stripBearer(bearerToken));
        String sub = claims.getSubject();
        return userManagementClient.selectDrawer(sub, drawerId)
                .flatMap(tokenExchangeService::exchange)
                .onErrorMap(e -> new GatewayException(HttpStatus.BAD_REQUEST, "Drawer selection failed: " + e.getMessage()));
    }

    private String stripBearer(String bearer) {
        return bearer.replace("Bearer ", "").trim();
    }

    public record DrawerFetchResult(List<DrawerDTO> drawers, String newJwt) {}
}
