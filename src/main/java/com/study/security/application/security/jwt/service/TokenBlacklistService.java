package com.study.security.application.security.jwt.service;

import com.study.security.application.security.jwt.provider.JwtTokenProvider;
import com.study.security.application.security.jwt.repository.TokenBlacklistStore;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final TokenBlacklistStore blacklistStore;
    private final JwtTokenProvider jwtTokenProvider;

    public void addToBlacklist(String token) {
        try {
            long ttl = jwtTokenProvider.getExpiresIn(token);
            blacklistStore.store(token, Duration.ofMillis(ttl));
        } catch (Exception e) {
            // todo : 블랙 리스트 등록 실패시 전략 생각해보기
            log.error("블랙 리스트 등록 실패", e);
        }
    }

    public boolean isBlacklisted(String token) {
        return blacklistStore.exists(token);
    }

}
