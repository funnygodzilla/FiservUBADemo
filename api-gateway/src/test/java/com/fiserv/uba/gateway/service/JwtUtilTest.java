package com.fiserv.uba.gateway.service;

import io.jsonwebtoken.Claims;
import java.util.List;
import org.junit.jupiter.api.Test;

class JwtUtilTest {

    @Test
    void tokenGenerationContainsEnrichedClaims() {
        JwtUtil util = new JwtUtil("VGhpc0lzQVN1ZmZpY2llbnRseUxvbmdTZWNyZXRLZXlGb3JKV1QxMjM0NTY=", 3600);
        String token = util.createToken("userX", "BR-1", "DR-1", "it-1", List.of("ROLE_TELLER"));
        Claims claims = util.parse(token);
        assert "userX".equals(claims.getSubject());
        assert "BR-1".equals(claims.get("branchId", String.class));
        assert "DR-1".equals(claims.get("drawerId", String.class));
    }
}
