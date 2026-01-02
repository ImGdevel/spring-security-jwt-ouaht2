package com.study.security.application.security.oauth.cookie;

import com.study.security.application.security.oauth.common.OAuth2CookieConstants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class OAuth2CookieProvider {

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

    private static void addCookie(HttpServletResponse response, String name, String value, String path, long maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                //.secure(true) todo: https 도입후 활성화 할 것
                .httpOnly(true)
                .path(path)
                .maxAge(maxAge)
                .sameSite(OAuth2CookieConstants.SAME_SITE_STRICT)
                .build();

        response.addHeader(OAuth2CookieConstants.SET_COOKIE_HEADER, cookie.toString());
    }

    private static Optional<String> getCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> name.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    private static void deleteCookie(HttpServletResponse response, String name, String path) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .path(path)
                .maxAge(0)
                .sameSite(OAuth2CookieConstants.SAME_SITE_STRICT)
                .build();

        response.addHeader(OAuth2CookieConstants.SET_COOKIE_HEADER, cookie.toString());
    }
}

