package com.study.security.application.security.jwt.logout;

import com.study.security.application.security.common.logout.LogoutAction;
import com.study.security.application.security.jwt.provider.JwtCookieProvider;
import com.study.security.application.security.jwt.service.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 로그아웃 시 Refresh Token을 블랙리스트에 등록하고 쿠키를 제거한다.
 * <p>
 * 필요 시 `spring.security.features.jwt=false`로 비활성화할 수 있다.
 */
@Component
@RequiredArgsConstructor
@Order(0)
@ConditionalOnProperty(prefix = "spring.security.features", name = "jwt", havingValue = "true", matchIfMissing = true)
public class JwtRefreshTokenLogoutAction implements LogoutAction {

    private final JwtCookieProvider jwtCookieProvider;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public void onLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        jwtCookieProvider.getRefreshTokenFromCookie(request)
                .ifPresent(tokenBlacklistService::addToBlacklist);

        jwtCookieProvider.deleteRefreshTokenCookie(response);
    }
}

