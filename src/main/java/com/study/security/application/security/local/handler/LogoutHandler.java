package com.study.security.application.security.local.handler;

import com.study.security.application.security.jwt.service.TokenBlacklistService;
import com.study.security.application.security.jwt.provider.JwtCookieProvider;
import com.study.security.application.security.common.util.SecurityResponseSender;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogoutHandler {

    private final SecurityResponseSender securityResponseSender;
    private final JwtCookieProvider jwtCookieProvider;
    private final TokenBlacklistService tokenBlacklistService;

    public void onLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {

        SecurityContextHolder.clearContext();

        jwtCookieProvider.getRefreshTokenFromCookie(request)
                .ifPresent(tokenBlacklistService::addToBlacklist);

        jwtCookieProvider.deleteRefreshTokenCookie(response);

        securityResponseSender.sendSuccess(response, HttpServletResponse.SC_OK, "로그아웃되었습니다.");
    }
}
