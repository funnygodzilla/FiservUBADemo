package com.fiserv.uba.gateway.config;

import com.fiserv.uba.gateway.util.LogUtils;
import com.fiserv.uba.gateway.util.TraceHeaderFilter;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import io.netty.channel.ChannelOption;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Configuration
@EnableConfigurationProperties(GatewayProperties.class)
public class WebClientConfig {

    @Bean
    public WebClient gatewayWebClient(
        GatewayProperties properties,
        ObservationRegistry observationRegistry,
        Tracer tracer
    ) {
        HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) properties.getConnectTimeout().toMillis())
            .responseTimeout(properties.getReadTimeout());

        return WebClient.builder()
            .baseUrl(properties.getBaseUrl())
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .filter(LogUtils.logRequest())
            .filter(LogUtils.logResponse())
            .filter(TraceHeaderFilter.addTracingHeaders(tracer))
            .filter(observationFilter(observationRegistry))
            .build();
    }

    private ExchangeFilterFunction observationFilter(ObservationRegistry registry) {
        return (request, next) -> Mono.defer(() -> {
            Observation observation = Observation.start("gateway.http", registry);
            observation.lowCardinalityKeyValue("http.method", request.method().name());
            observation.lowCardinalityKeyValue("http.url", request.url().getPath());
            return next.exchange(request)
                .doOnError(observation::error)
                .doFinally(signal -> observation.stop());
        });
    }
}
