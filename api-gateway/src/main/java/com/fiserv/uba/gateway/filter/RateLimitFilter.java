package com.fiserv.uba.gateway.filter;

import com.fiserv.uba.gateway.exception.GatewayException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;

@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    private static final int MAX_REQUESTS_PER_MINUTE = 120;
    private final Map<String, CounterWindow> fallbackCounters = new ConcurrentHashMap<>();
    private final ReactiveStringRedisTemplate redisTemplate;

    public RateLimitFilter(org.springframework.beans.factory.ObjectProvider<ReactiveStringRedisTemplate> redisTemplateProvider) {
        this.redisTemplate = redisTemplateProvider.getIfAvailable();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String key = exchange.getRequest().getRemoteAddress() == null
                ? "unknown"
                : exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();

        if (redisTemplate != null) {
            String redisKey = "ratelimit:" + key + ":" + (Instant.now().getEpochSecond() / 60);
            return redisTemplate.opsForValue().increment(redisKey)
                    .flatMap(count -> {
                        if (count == 1) {
                            return redisTemplate.expire(redisKey, java.time.Duration.ofMinutes(2)).thenReturn(count);
                        }
                        return Mono.just(count);
                    })
                    .flatMap(count -> count > MAX_REQUESTS_PER_MINUTE
                            ? Mono.error(new GatewayException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded"))
                            : chain.filter(exchange));
        }

        CounterWindow window = fallbackCounters.computeIfAbsent(key, k -> new CounterWindow());
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
