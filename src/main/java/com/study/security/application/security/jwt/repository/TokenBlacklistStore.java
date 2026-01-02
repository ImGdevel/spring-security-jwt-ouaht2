package com.study.security.application.security.jwt.repository;

import java.time.Duration;

/**
 * 블랙리스트 저장소 포트.
 */
public interface TokenBlacklistStore {
    void store(String token, Duration ttl);
    boolean exists(String token);
}
