package com.fiserv.uba.gateway.service;

import com.fiserv.uba.gateway.dto.UpdatedUserContextDTO;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;

class TokenExchangeServiceTest {

    private RedisSessionOperations redisSessionService;
    private TokenExchangeService service;
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("VGhpc0lzQVN1ZmZpY2llbnRseUxvbmdTZWNyZXRLZXlGb3JKV1QxMjM0NTY=", 3600);
        redisSessionService = Mockito.mock(RedisSessionOperations.class);
        service = new TokenExchangeService(jwtUtil, redisSessionService);
        Mockito.when(redisSessionService.clearDrawerScoped(any(), any(), any())).thenReturn(Mono.just(true));
        Mockito.when(redisSessionService.save(any(), any())).thenReturn(Mono.just(true));
    }

    @Test
    void storesSessionAndReturnsToken() {
        UpdatedUserContextDTO ctx = new UpdatedUserContextDTO("u1", "b1", "d1", "it1", List.of("ROLE_TELLER"));

        StepVerifier.create(service.exchange(ctx))
                .assertNext(token -> {
                    assert token != null && !token.isBlank();
                }).verifyComplete();
    }

    @Test
    void redisKeyFormat() {
        RedisSessionService real = new RedisSessionService(null, new com.fasterxml.jackson.databind.ObjectMapper());
        assert "session:u1:b1:d1".equals(real.sessionKey("u1", "b1", "d1"));
    }
}
