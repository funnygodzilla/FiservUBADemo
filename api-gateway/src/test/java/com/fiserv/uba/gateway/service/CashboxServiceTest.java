package com.fiserv.uba.gateway.service;

import com.fiserv.uba.gateway.client.EsfClient;
import com.fiserv.uba.gateway.dto.CashBoxDTO;
import com.fiserv.uba.gateway.model.UserSessionContext;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class CashboxServiceTest {

    @Test
    void validatesRedisSessionBeforeDelegating() {
        JwtUtil jwtUtil = new JwtUtil("VGhpc0lzQVN1ZmZpY2llbnRseUxvbmdTZWNyZXRLZXlGb3JKV1QxMjM0NTY=", 3600);
        RedisSessionOperations redis = Mockito.mock(RedisSessionOperations.class);
        EsfClient esf = Mockito.mock(EsfClient.class);

        String token = jwtUtil.createToken("u1", "b1", "d1", "it1", List.of("ROLE_TELLER"));
        Mockito.when(redis.get("u1", "b1", "d1")).thenReturn(Mono.just(new UserSessionContext("u1", "b1", "d1", "it1", List.of(), List.of(), Map.of(), List.of())));
        Mockito.when(esf.getCashBoxDetails("Bearer " + token)).thenReturn(Mono.just(new CashBoxDTO("d1", "b1", BigDecimal.ONE)));

        CashboxService service = new CashboxService(jwtUtil, redis, esf);

        StepVerifier.create(service.getCashbox("Bearer " + token))
                .expectNextMatches(dto -> dto.availableCash().compareTo(BigDecimal.ONE) == 0)
                .verifyComplete();
    }
}
