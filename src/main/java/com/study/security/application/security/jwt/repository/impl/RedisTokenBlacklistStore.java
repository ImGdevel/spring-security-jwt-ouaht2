package com.study.security.application.security.jwt.repository.impl;

import com.study.security.application.security.jwt.repository.TokenBlacklistStore;
import com.study.security.infra.redis.adapter.RedisService;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Redis를 이용한 토큰 블랙리스트 저장소 구현체
 */
@Component
@RequiredArgsConstructor
public class RedisTokenBlacklistStore implements TokenBlacklistStore {

    private static final String BLACKLISTED = "1";
    private static final String BLACKLIST_PREFIX = "blacklist:refresh-token:";

    private final RedisService redisService;

    @Override
    public void store(String token, Duration ttl) {
        String key = buildKey(token);
        redisService.save(key, BLACKLISTED, ttl);
    }

    @Override
    public boolean exists(String token) {
        String key = buildKey(token);
        return redisService.find(key).isPresent();
    }

    private String buildKey(String token) {
        return BLACKLIST_PREFIX + DigestUtils.sha256Hex(token);
    }
}
