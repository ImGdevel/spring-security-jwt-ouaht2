package com.study.security.application.security.jwt.service;

import com.study.security.application.security.jwt.provider.JwtTokenProvider;
import com.study.security.application.security.jwt.repository.TokenBlacklistStore;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private static final String BLACKLIST_PREFIX = "blacklist:refresh-token:";
    private final TokenBlacklistStore blacklistStore;
    private final JwtTokenProvider jwtTokenProvider;

    public void addToBlacklist(String token) {
        try {
            long ttl = jwtTokenProvider.getExpiresIn(token);
            String key = generateRedisKey(token);
            blacklistStore.store(key, Duration.ofMillis(ttl));
        } catch (Exception e) {
            // todo : 블랙 리스트 등록 실패시 전략 생각해보기
            log.error("블랙 리스트 등록 실패", e);
        }
    }

    public boolean isBlacklisted(String token) {
        String key = generateRedisKey(token);
        return blacklistStore.exists(key);
    }

    private String generateRedisKey(String token){
        return BLACKLIST_PREFIX + DigestUtils.sha256Hex(token);
    }
}
