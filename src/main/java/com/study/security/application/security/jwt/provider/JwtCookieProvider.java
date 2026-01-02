package com.study.security.application.security.jwt.provider;

import com.study.security.application.security.config.properties.SecurityCookieProperties;
import com.study.security.application.security.jwt.common.JwtCookieConstants;
import com.study.security.application.security.jwt.properties.JwtProperties;
import com.study.security.application.security.common.constants.SecurityConstants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtCookieProvider {

    private final JwtProperties jwtProperties;
    private final SecurityCookieProperties securityCookieProperties;

    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        long maxAgeInSeconds = jwtProperties.getRefreshTokenExpiration() / 1000;
        addCookie(response, JwtCookieConstants.REFRESH_TOKEN_COOKIE_NAME, refreshToken,
                  SecurityConstants.AUTH_COOKIE_PATH, maxAgeInSeconds);
    }

    public Optional<String> getRefreshTokenFromCookie(HttpServletRequest request) {
        return getCookie(request, JwtCookieConstants.REFRESH_TOKEN_COOKIE_NAME);
    }

    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        deleteCookie(response, JwtCookieConstants.REFRESH_TOKEN_COOKIE_NAME, SecurityConstants.AUTH_COOKIE_PATH);
    }

    private void addCookie(HttpServletResponse response, String name, String value, String path, long maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .secure(securityCookieProperties.isSecure())
                .httpOnly(true)
                .path(path)
                .maxAge(maxAge)
                .sameSite(JwtCookieConstants.SAME_SITE_STRICT)
                .build();

        response.addHeader(JwtCookieConstants.SET_COOKIE_HEADER, cookie.toString());
    }

    private Optional<String> getCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> name.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    private void deleteCookie(HttpServletResponse response, String name, String path) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .secure(securityCookieProperties.isSecure())
                .httpOnly(true)
                .path(path)
                .maxAge(0)
                .sameSite(JwtCookieConstants.SAME_SITE_STRICT)
                .build();

        response.addHeader(JwtCookieConstants.SET_COOKIE_HEADER, cookie.toString());
    }
}
