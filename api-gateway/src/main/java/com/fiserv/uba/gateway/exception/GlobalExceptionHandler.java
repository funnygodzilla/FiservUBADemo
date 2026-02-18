package com.fiserv.uba.gateway.exception;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GatewayException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleGateway(GatewayException ex) {
        return Mono.just(ResponseEntity.status(ex.getStatus()).body(Map.of("error", ex.getMessage())));
    }
}
