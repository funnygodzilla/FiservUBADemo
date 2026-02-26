package com.fiserv.uba.gateway.filter;

import com.fiserv.uba.gateway.exception.GatewayException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    private static final int MAX_REQUESTS_PER_MINUTE = 120;
    private final Map<String, CounterWindow> counters = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String key = exchange.getRequest().getRemoteAddress() == null
                ? "unknown"
                : exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();

        CounterWindow window = counters.computeIfAbsent(key, k -> new CounterWindow());
        long now = Instant.now().getEpochSecond();
        if (now - window.windowStartEpochSecond > 60) {
            window.reset(now);
        }

        if (window.counter.incrementAndGet() > MAX_REQUESTS_PER_MINUTE) {
            return Mono.error(new GatewayException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded"));
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -250;
    }

    private static class CounterWindow {
        private long windowStartEpochSecond = Instant.now().getEpochSecond();
        private final AtomicInteger counter = new AtomicInteger(0);

        private void reset(long epochSecond) {
            this.windowStartEpochSecond = epochSecond;
            this.counter.set(0);
        }
    }
}
