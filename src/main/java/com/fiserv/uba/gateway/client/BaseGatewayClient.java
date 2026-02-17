package com.fiserv.uba.gateway.client;

import com.fiserv.uba.gateway.exception.GatewayClientException;
import com.fiserv.uba.gateway.exception.GatewayServerException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public abstract class BaseGatewayClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseGatewayClient.class);

    protected final WebClient webClient;

    protected BaseGatewayClient(WebClient webClient) {
        this.webClient = webClient;
    }

    protected <T> Mono<T> post(String uri, Object body, Map<String, String> headers, Class<T> responseType) {
        return webClient.post()
            .uri(uri)
            .headers(httpHeaders -> httpHeaders.setAll(headers))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response -> handleError(response.statusCode(), response.bodyToMono(String.class)))
            .onStatus(HttpStatus::is5xxServerError, response -> handleError(response.statusCode(), response.bodyToMono(String.class)))
            .bodyToMono(responseType);
    }

    protected <T> Mono<T> put(String uri, Object body, Map<String, String> headers, Class<T> responseType) {
        return webClient.put()
            .uri(uri)
            .headers(httpHeaders -> httpHeaders.setAll(headers))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response -> handleError(response.statusCode(), response.bodyToMono(String.class)))
            .onStatus(HttpStatus::is5xxServerError, response -> handleError(response.statusCode(), response.bodyToMono(String.class)))
            .bodyToMono(responseType);
    }

    protected <T> Mono<T> get(String uri, Map<String, String> headers, Class<T> responseType) {
        return webClient.get()
            .uri(uri)
            .headers(httpHeaders -> httpHeaders.setAll(headers))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, response -> handleError(response.statusCode(), response.bodyToMono(String.class)))
            .onStatus(HttpStatus::is5xxServerError, response -> handleError(response.statusCode(), response.bodyToMono(String.class)))
            .bodyToMono(responseType);
    }

    protected Mono<? extends Throwable> handleError(HttpStatus status, Mono<String> body) {
        return body.defaultIfEmpty("").map(message -> {
            String errorMessage = String.format("Gateway error status=%s message=%s", status, message);
            LOGGER.warn(errorMessage);
            if (status.is4xxClientError()) {
                return new GatewayClientException(errorMessage);
            }
            return new GatewayServerException(errorMessage);
        });
    }

    protected HttpHeaders toHeaders(Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAll(headers);
        return httpHeaders;
    }
}
