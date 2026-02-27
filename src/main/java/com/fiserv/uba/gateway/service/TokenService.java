package com.fiserv.uba.gateway.service;

import com.fiserv.uba.gateway.client.BankingGatewayClient;
import com.fiserv.uba.gateway.config.GatewayProperties;
import com.fiserv.uba.gateway.dto.ApplicationTokenRequest;
import com.fiserv.uba.gateway.dto.ApplicationTokenResponse;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenService.class);

    private final BankingGatewayClient gatewayClient;
    private final GatewayProperties properties;
    private final AtomicReference<CachedToken> cachedToken = new AtomicReference<>();

    public TokenService(BankingGatewayClient gatewayClient, GatewayProperties properties) {
        this.gatewayClient = gatewayClient;
        this.properties = properties;
    }

    public Mono<ApplicationTokenResponse> getApplicationToken() {
        CachedToken existing = cachedToken.get();
        if (existing != null && !existing.isExpired()) {
            return Mono.just(existing.token());
        }

        ApplicationTokenRequest request = new ApplicationTokenRequest(
            properties.getClientId(),
            properties.getClientSecret(),
            properties.getScope()
        );

        return gatewayClient.requestApplicationToken(request)
            .doOnNext(token -> {
                long expirySeconds = Math.max(30, token.getExpiresIn() - 30);
                cachedToken.set(new CachedToken(token, Duration.ofSeconds(expirySeconds)));
                LOGGER.debug("Cached new application token expiring in {} seconds", expirySeconds);
            });
    }

    private static final class CachedToken {
        private final ApplicationTokenResponse token;
        private final long expiresAtMillis;

        private CachedToken(ApplicationTokenResponse token, Duration ttl) {
            this.token = token;
            this.expiresAtMillis = System.currentTimeMillis() + ttl.toMillis();
        }

        private boolean isExpired() {
            return System.currentTimeMillis() >= expiresAtMillis;
        }

        private ApplicationTokenResponse token() {
            return token;
        }
    }
}
