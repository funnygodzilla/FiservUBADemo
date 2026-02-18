package com.fiserv.uba.gateway.service;

import com.fiserv.uba.gateway.model.UserSessionContext;
import java.time.Duration;
import reactor.core.publisher.Mono;

public interface RedisSessionOperations {
    String sessionKey(String userId, String branchId, String drawerId);
    Mono<Boolean> save(UserSessionContext context, Duration ttl);
    Mono<UserSessionContext> get(String userId, String branchId, String drawerId);
    Mono<Boolean> clearDrawerScoped(String userId, String branchId, String drawerId);
}
