package com.fiserv.uba.gateway.util;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ClientRequest;
import reactor.core.publisher.Mono;

public final class TraceHeaderFilter {

    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String SPAN_ID_HEADER = "X-Span-Id";

    private TraceHeaderFilter() {
    }

    public static ExchangeFilterFunction addTracingHeaders(Tracer tracer) {
        return (request, next) -> Mono.defer(() -> {
            Span span = tracer.currentSpan();
            if (span == null || span.context() == null) {
                return next.exchange(request);
            }

            ClientRequest updated = ClientRequest.from(request)
                .header(TRACE_ID_HEADER, span.context().traceId())
                .header(SPAN_ID_HEADER, span.context().spanId())
                .build();
            return next.exchange(updated);
        });
    }
}
