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

/**
 * OAuth2 인증 요청 쿠키 관리 제공자
 */
@Component
@RequiredArgsConstructor
public class OAuth2CookieProvider {

    private final SecurityCookieProperties securityCookieProperties;

    /**
     * OAuth2 인증 요청 정보를 직렬화해서 쿠키로 저장합니다.
     */
    public void addOAuth2AuthorizationRequestCookie(HttpServletResponse response, String value) {
        addCookie(
                response,
                OAuth2CookieConstants.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                value,
                "/",
                OAuth2CookieConstants.OAUTH2_COOKIE_EXPIRE_SECONDS
        );
    }

    /**
     * 쿠키에서 OAuth2 인증 요청 데이터를 복원합니다.
     */
    public Optional<String> getOAuth2AuthorizationRequestCookie(HttpServletRequest request) {
        return getCookie(request, OAuth2CookieConstants.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
    }

    /**
     * OAuth2 인증 요청 쿠키를 무효화합니다.
     */
    public void deleteOAuth2AuthorizationRequestCookie(HttpServletResponse response) {
        deleteCookie(response, OAuth2CookieConstants.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, "/");
    }

    /**
     * OAuth2 인증 중 리다이렉트할 URI를 쿠키에 저장합니다.
     */
    public void addRedirectUriCookie(HttpServletResponse response, String redirectUri) {
        addCookie(
                response,
                OAuth2CookieConstants.REDIRECT_URI_COOKIE_NAME,
                redirectUri,
                "/",
                OAuth2CookieConstants.OAUTH2_COOKIE_EXPIRE_SECONDS
        );
    }

    /**
     * 저장된 OAuth2 리다이렉트 URI를 조회합니다.
     */
    public Optional<String> getRedirectUriCookie(HttpServletRequest request) {
        return getCookie(request, OAuth2CookieConstants.REDIRECT_URI_COOKIE_NAME);
    }

    /**
     * OAuth2 리다이렉트 URI 쿠키를 삭제합니다.
     */
    public void deleteRedirectUriCookie(HttpServletResponse response) {
        deleteCookie(response, OAuth2CookieConstants.REDIRECT_URI_COOKIE_NAME, "/");
    }

    //////////////////////////////////////////////////////////////////////////////

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
