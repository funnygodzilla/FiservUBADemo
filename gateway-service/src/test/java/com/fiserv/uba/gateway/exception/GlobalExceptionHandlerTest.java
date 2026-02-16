package com.fiserv.uba.gateway.exception;

import com.fiserv.uba.gateway.dto.ResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleGatewayClientException() {
        GatewayClientException exception = new GatewayClientException("Invalid request");
        
        Mono<ResponseEntity<ResponseDTO<Void>>> result = handler.handleGatewayClientException(exception);
        
        StepVerifier.create(result)
            .assertNext(response -> {
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertNotNull(response.getBody());
                assertEquals(400, response.getBody().getStatus());
                assertEquals("Invalid request", response.getBody().getMessage());
                assertNull(response.getBody().getData());
            })
            .verifyComplete();
    }

    @Test
    void testHandleGatewayServerException() {
        GatewayServerException exception = new GatewayServerException("Server error occurred");
        
        Mono<ResponseEntity<ResponseDTO<Void>>> result = handler.handleGatewayServerException(exception);
        
        StepVerifier.create(result)
            .assertNext(response -> {
                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                assertNotNull(response.getBody());
                assertEquals(500, response.getBody().getStatus());
                // Should not leak internal details
                assertEquals("An internal error occurred. Please try again later.", response.getBody().getMessage());
                assertNull(response.getBody().getData());
            })
            .verifyComplete();
    }

    @Test
    void testHandleGenericException() {
        Exception exception = new RuntimeException("Unexpected error");
        
        Mono<ResponseEntity<ResponseDTO<Void>>> result = handler.handleGenericException(exception);
        
        StepVerifier.create(result)
            .assertNext(response -> {
                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                assertNotNull(response.getBody());
                assertEquals(500, response.getBody().getStatus());
                // Should not leak internal details
                assertEquals("An unexpected error occurred. Please try again later.", response.getBody().getMessage());
                assertNull(response.getBody().getData());
            })
            .verifyComplete();
    }
}
