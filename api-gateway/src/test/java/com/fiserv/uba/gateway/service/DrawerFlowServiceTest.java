package com.fiserv.uba.gateway.service;

import com.fiserv.uba.gateway.client.UserManagementClient;
import com.fiserv.uba.gateway.dto.DrawerDTO;
import com.fiserv.uba.gateway.dto.UpdatedUserContextDTO;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class DrawerFlowServiceTest {

    private JwtUtil jwtUtil;
    private UserManagementClient userManagementClient;
    private TokenExchangeOperations tokenExchangeService;
    private DrawerFlowService service;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("VGhpc0lzQVN1ZmZpY2llbnRseUxvbmdTZWNyZXRLZXlGb3JKV1QxMjM0NTY=", 3600);
        userManagementClient = Mockito.mock(UserManagementClient.class);
        tokenExchangeService = Mockito.mock(TokenExchangeOperations.class);
        service = new DrawerFlowService(jwtUtil, userManagementClient, tokenExchangeService);
    }

    @Test
    void singleDrawerAutoEnrichment() {
        String token = jwtUtil.createToken("user1", "", "", "", List.of("ROLE_TELLER"));
        Mockito.when(userManagementClient.getDrawers("user1", "corr-1")).thenReturn(Mono.just(List.of(new DrawerDTO("D1", "B1", "main"))));
        Mockito.when(userManagementClient.selectDrawer("user1", "D1", "corr-1")).thenReturn(Mono.just(new UpdatedUserContextDTO("user1", "B1", "D1", "it1", List.of("ROLE_TELLER"))));
        Mockito.when(tokenExchangeService.exchange(any())).thenReturn(Mono.just("new.jwt"));

        StepVerifier.create(service.fetchDrawers("Bearer " + token, "corr-1"))
                .assertNext(result -> {
                    assert result.drawers().size() == 1;
                    assert "new.jwt".equals(result.newJwt());
                }).verifyComplete();
    }

    @Test
    void multiDrawerNoAutoEnrichment() {
        String token = jwtUtil.createToken("user2", "", "", "", List.of("ROLE_TELLER"));
        Mockito.when(userManagementClient.getDrawers("user2", "corr-1")).thenReturn(Mono.just(List.of(
                new DrawerDTO("D1", "B1", "main"), new DrawerDTO("D2", "B1", "backup"))));

        StepVerifier.create(service.fetchDrawers("Bearer " + token, "corr-1"))
                .assertNext(result -> {
                    assert result.drawers().size() == 2;
                    assert result.newJwt() == null;
                }).verifyComplete();

        Mockito.verify(userManagementClient, Mockito.never()).selectDrawer(eq("user2"), any(), any());
    }

    @Test
    void selectionErrorFlowReturnsFailure() {
        String token = jwtUtil.createToken("user3", "", "", "", List.of("ROLE_TELLER"));
        Mockito.when(userManagementClient.selectDrawer("user3", "D9", "corr-1")).thenReturn(Mono.error(new RuntimeException("down")));
        StepVerifier.create(service.selectDrawer("Bearer " + token, "D9", "corr-1"))
                .expectError()
                .verify();
    }
}
