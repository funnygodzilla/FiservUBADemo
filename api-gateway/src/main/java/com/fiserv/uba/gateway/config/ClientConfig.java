package com.fiserv.uba.gateway.config;

import com.fiserv.uba.gateway.config.properties.DownstreamProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(DownstreamProperties.class)
public class ClientConfig {

    @Bean("userManagementWebClient")
    public WebClient userManagementWebClient(DownstreamProperties properties) {
        return baseWebClient(properties.getUserManagementBaseUrl());
    }

    @Bean("esfWebClient")
    public WebClient esfWebClient(DownstreamProperties properties) {
        return baseWebClient(properties.getEsfBaseUrl());
    }

    private WebClient baseWebClient(String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .exchangeStrategies(ExchangeStrategies.withDefaults())
                .build();
    }
}
