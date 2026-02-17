package com.fiserv.uba.gateway.util;

import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

public final class LogUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.class);

    private LogUtils() {
    }

    public static ExchangeFilterFunction logRequest() {
        return (request, next) -> {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Gateway request: {} {} headers={}",
                    request.method(),
                    request.url(),
                    request.headers().toSingleValueMap());
            }
            return next.exchange(request);
        };
    }

    public static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            if (LOGGER.isDebugEnabled()) {
                String headers = response.headers().asHttpHeaders().entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining(","));
                LOGGER.debug("Gateway response: status={} headers={}", response.statusCode(), headers);
            }
            return Mono.just(response);
        });
    }
}
