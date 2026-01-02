package com.study.security.application.security.service;

import com.study.security.application.member.MemberPort;
import com.study.security.application.security.util.JwtTokenProvider;
import com.study.security.common.exception.BusinessException;
import com.study.security.common.exception.code.AuthErrorCode;
import com.study.security.common.exception.code.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenRefreshService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberPort memberPort;
    private final TokenBlacklistService tokenBlacklistService;

    public String refreshAccessToken(String refreshToken) {
        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new BusinessException(AuthErrorCode.REFRESH_TOKEN_INVALID);
        }

        if (jwtTokenProvider.isTokenExpired(refreshToken)) {
            throw new BusinessException(AuthErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        if (tokenBlacklistService.isBlacklisted(refreshToken)) {
            throw new BusinessException(AuthErrorCode.REFRESH_TOKEN_INVALID);
        }

        Long memberId = jwtTokenProvider.getUidFromToken(refreshToken);
        var member = memberPort.findById(memberId)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.USER_NOT_FOUND));

        if (!member.active()) {
            throw new BusinessException(MemberErrorCode.MEMBER_INACTIVE);
        }

        return jwtTokenProvider.generateAccessToken(memberId, member.role().name());
    }
}
