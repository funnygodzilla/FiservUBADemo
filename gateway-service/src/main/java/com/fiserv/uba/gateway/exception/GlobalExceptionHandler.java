package com.fiserv.uba.gateway.exception;

import com.fiserv.uba.gateway.dto.ResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(GatewayClientException.class)
    public Mono<ResponseEntity<ResponseDTO<Void>>> handleGatewayClientException(GatewayClientException ex) {
        logger.warn("Client error: {}", ex.getMessage());
        
        ResponseDTO<Void> response = new ResponseDTO<>(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            null
        );
        
        return Mono.just(ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response));
    }

    @ExceptionHandler(GatewayServerException.class)
    public Mono<ResponseEntity<ResponseDTO<Void>>> handleGatewayServerException(GatewayServerException ex) {
        logger.error("Server error: {}", ex.getMessage(), ex);
        
        ResponseDTO<Void> response = new ResponseDTO<>(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An internal error occurred. Please try again later.",
            null
        );
        
        return Mono.just(ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ResponseDTO<Void>>> handleValidationException(WebExchangeBindException ex) {
        logger.warn("Validation error: {}", ex.getMessage());
        
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .reduce((a, b) -> a + "; " + b)
            .orElse("Validation failed");
        
        ResponseDTO<Void> response = new ResponseDTO<>(
            HttpStatus.BAD_REQUEST.value(),
            errorMessage,
            null
        );
        
        return Mono.just(ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ResponseDTO<Void>>> handleGenericException(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        
        ResponseDTO<Void> response = new ResponseDTO<>(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred. Please try again later.",
            null
        );
        
        return Mono.just(ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response));
    }
}
