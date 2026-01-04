package com.study.security.application.security.jwt.service;

import com.study.security.application.security.user.repository.UserRepositoryPort;
import com.study.security.application.security.jwt.provider.JwtTokenProvider;
import com.study.security.common.exception.BusinessException;
import com.study.security.common.exception.code.AuthErrorCode;
import com.study.security.common.exception.code.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 토큰 리프레시 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenRefreshService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepositoryPort userRepositoryPort;
    private final TokenBlacklistService tokenBlacklistService;

    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급합니다.
     *
     * @param refreshToken 리프레시 토큰
     * @return 새로운 액세스 토큰
     * @throws BusinessException 리프레시 토큰이 유효하지 않거나 만료된 경우, 또는 사용자가 존재하지 않거나 비활성화된 경우 발생
     */
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
        var member = userRepositoryPort.findById(memberId)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.USER_NOT_FOUND));

        if (!member.active()) {
            throw new BusinessException(MemberErrorCode.MEMBER_INACTIVE);
        }

        return jwtTokenProvider.generateAccessToken(memberId, member.role().name());
    }
}
