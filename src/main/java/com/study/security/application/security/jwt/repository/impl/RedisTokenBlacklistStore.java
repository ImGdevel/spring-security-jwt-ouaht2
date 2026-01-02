package com.study.security.application.security.jwt.repository.impl;

import com.study.security.application.security.jwt.repository.TokenBlacklistStore;
import com.study.security.infra.redis.adapter.RedisService;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisTokenBlacklistStore implements TokenBlacklistStore {

    private static final String BLACKLISTED = "1";

    private final RedisService redisService;

    @Override
    public void store(String key, Duration ttl) {
        redisService.save(key, BLACKLISTED, ttl);
    }

    @Override
    public boolean exists(String key) {
        return redisService.find(key).isPresent();
    }
}
