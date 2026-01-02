package com.study.security.application.security.oauth.cookie;

import com.study.security.application.security.config.properties.SecurityCookieProperties;
import com.study.security.application.security.oauth.common.OAuth2CookieConstants;
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
public class OAuth2CookieProvider {

    private final SecurityCookieProperties securityCookieProperties;

    public void addOAuth2AuthorizationRequestCookie(HttpServletResponse response, String value) {
        addCookie(
                response,
                OAuth2CookieConstants.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                value,
                "/",
                OAuth2CookieConstants.OAUTH2_COOKIE_EXPIRE_SECONDS
        );
    }

    public Optional<String> getOAuth2AuthorizationRequestCookie(HttpServletRequest request) {
        return getCookie(request, OAuth2CookieConstants.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
    }

    public void deleteOAuth2AuthorizationRequestCookie(HttpServletResponse response) {
        deleteCookie(response, OAuth2CookieConstants.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, "/");
    }

    public void addRedirectUriCookie(HttpServletResponse response, String redirectUri) {
        addCookie(
                response,
                OAuth2CookieConstants.REDIRECT_URI_COOKIE_NAME,
                redirectUri,
                "/",
                OAuth2CookieConstants.OAUTH2_COOKIE_EXPIRE_SECONDS
        );
    }

    public Optional<String> getRedirectUriCookie(HttpServletRequest request) {
        return getCookie(request, OAuth2CookieConstants.REDIRECT_URI_COOKIE_NAME);
    }

    public void deleteRedirectUriCookie(HttpServletResponse response) {
        deleteCookie(response, OAuth2CookieConstants.REDIRECT_URI_COOKIE_NAME, "/");
    }

    private void addCookie(HttpServletResponse response, String name, String value, String path, long maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .secure(securityCookieProperties.isSecure())
                .httpOnly(true)
                .path(path)
                .maxAge(maxAge)
                .sameSite(securityCookieProperties.getOauth2SameSite())
                .build();

        response.addHeader(OAuth2CookieConstants.SET_COOKIE_HEADER, cookie.toString());
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
                .sameSite(securityCookieProperties.getOauth2SameSite())
                .build();

        response.addHeader(OAuth2CookieConstants.SET_COOKIE_HEADER, cookie.toString());
    }
}
