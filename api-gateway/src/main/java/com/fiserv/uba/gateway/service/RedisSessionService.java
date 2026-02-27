package com.fiserv.uba.gateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiserv.uba.gateway.model.UserSessionContext;
import java.time.Duration;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RedisSessionService implements RedisSessionOperations {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisSessionService(ReactiveStringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public String sessionKey(String userId, String branchId, String drawerId) {
        return String.format("session:%s:%s:%s", userId, branchId, drawerId);
    }

    public Mono<Boolean> save(UserSessionContext context, Duration ttl) {
        try {
            String payload = objectMapper.writeValueAsString(context);
            return redisTemplate.opsForValue().set(sessionKey(context.userId(), context.branchId(), context.drawerId()), payload, ttl);
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }

    public Mono<UserSessionContext> get(String userId, String branchId, String drawerId) {
        return redisTemplate.opsForValue().get(sessionKey(userId, branchId, drawerId))
                .flatMap(raw -> {
                    try {
                        return Mono.just(objectMapper.readValue(raw, UserSessionContext.class));
                    } catch (JsonProcessingException e) {
                        return Mono.error(e);
                    }
                });
    }

    public Mono<Boolean> clearDrawerScoped(String userId, String branchId, String drawerId) {
        return redisTemplate.delete(sessionKey(userId, branchId, drawerId)).map(deleted -> deleted > 0);
    }
}
