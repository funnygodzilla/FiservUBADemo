package com.fiserv.uba.gateway.service;

import com.fiserv.uba.gateway.dto.UpdatedUserContextDTO;
import reactor.core.publisher.Mono;

public interface TokenExchangeOperations {
    Mono<String> exchange(UpdatedUserContextDTO ctx);
}
