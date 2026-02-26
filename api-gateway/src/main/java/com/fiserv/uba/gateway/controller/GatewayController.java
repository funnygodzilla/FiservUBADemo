package com.fiserv.uba.gateway.controller;

import com.fiserv.uba.gateway.dto.CashBoxDTO;
import com.fiserv.uba.gateway.dto.DrawerDTO;
import com.fiserv.uba.gateway.service.CashboxService;
import com.fiserv.uba.gateway.service.DrawerFlowService;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping
public class GatewayController {

    private final DrawerFlowService drawerFlowService;
    private final CashboxService cashboxService;

    public GatewayController(DrawerFlowService drawerFlowService, CashboxService cashboxService) {
        this.drawerFlowService = drawerFlowService;
        this.cashboxService = cashboxService;
    }

    @GetMapping("/api/drawers")
    public Mono<ResponseEntity<List<DrawerDTO>>> getDrawers(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return drawerFlowService.fetchDrawers(authorization)
                .map(result -> {
                    ResponseEntity.BodyBuilder builder = ResponseEntity.ok();
                    if (result.newJwt() != null) {
                        builder.header("X-New-JWT", result.newJwt());
                    }
                    return builder.body(result.drawers());
                });
    }

    @PostMapping("/api/drawer/select/{drawerId}")
    public Mono<ResponseEntity<Void>> selectDrawer(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                                   @PathVariable String drawerId) {
        return drawerFlowService.selectDrawer(authorization, drawerId)
                .map(newToken -> ResponseEntity.ok().header("X-New-JWT", newToken).build());
    }

    @GetMapping("/cashbox/details")
    public Mono<ResponseEntity<CashBoxDTO>> getCashbox(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return cashboxService.getCashbox(authorization).map(ResponseEntity::ok);
    }
}
