package com.study.security.application.security.oauth.repository;

import com.study.security.application.security.common.util.RedirectUriValidator;
import com.study.security.application.security.oauth.common.OAuth2CookieConstants;
import com.study.security.application.security.oauth.cookie.OAuth2CookieProvider;
import com.study.security.common.utils.SerializationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@Component
@RequiredArgsConstructor
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private final RedirectUriValidator redirectUriValidator;
    private final OAuth2CookieProvider oAuth2CookieProvider;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return oAuth2CookieProvider.getOAuth2AuthorizationRequestCookie(request)
                .map(value -> SerializationUtil.deserialize(value, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            removeAuthorizationRequestCookies(response);
            return;
        }
        oAuth2CookieProvider.addOAuth2AuthorizationRequestCookie(response, SerializationUtil.serialize(authorizationRequest));

        String redirectUriAfterLogin = request.getParameter(OAuth2CookieConstants.REDIRECT_URI_COOKIE_NAME);
        if (StringUtils.hasText(redirectUriAfterLogin) && redirectUriValidator.isValidRedirectUri(redirectUriAfterLogin)) {
            oAuth2CookieProvider.addRedirectUriCookie(response, redirectUriAfterLogin);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return loadAuthorizationRequest(request);
    }

    public void removeAuthorizationRequestCookies(HttpServletResponse response) {
        oAuth2CookieProvider.deleteOAuth2AuthorizationRequestCookie(response);
        oAuth2CookieProvider.deleteRedirectUriCookie(response);
    }
}
