package com.fiserv.uba.gateway.client.impl;

import com.fiserv.uba.gateway.client.UserManagementClient;
import com.fiserv.uba.gateway.dto.DrawerDTO;
import com.fiserv.uba.gateway.dto.UpdatedUserContextDTO;
import com.fiserv.uba.gateway.exception.GatewayException;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserManagementHttpClient implements UserManagementClient {

    private final WebClient webClient;

    public UserManagementHttpClient(@Qualifier("userManagementWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<List<DrawerDTO>> getDrawers(String sub) {
        return webClient.get()
                .uri("/users/{sub}/drawers", sub)
                .retrieve()
                .onStatus(status -> status.isError(), r -> r.bodyToMono(String.class)
                        .defaultIfEmpty("user-management unavailable")
                        .flatMap(m -> Mono.error(new GatewayException(HttpStatus.BAD_REQUEST, m))))
                .bodyToMono(new ParameterizedTypeReference<List<DrawerDTO>>() {});
    }

    @Override
    public Mono<UpdatedUserContextDTO> selectDrawer(String sub, String drawerId) {
        return webClient.post()
                .uri("/users/{sub}/drawer/select/{drawerId}", sub, drawerId)
                .retrieve()
                .onStatus(status -> status.isError(), r -> r.bodyToMono(String.class)
                        .defaultIfEmpty("drawer selection failed")
                        .flatMap(m -> Mono.error(new GatewayException(HttpStatus.BAD_REQUEST, m))))
                .bodyToMono(UpdatedUserContextDTO.class);
    }
}
