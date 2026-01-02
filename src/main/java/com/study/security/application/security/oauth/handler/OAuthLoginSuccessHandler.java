package com.study.security.application.security.oauth.handler;

import com.study.security.application.security.oauth.dto.CustomOAuthUserDetails;
import com.study.security.application.security.oauth.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.study.security.application.security.jwt.provider.JwtCookieProvider;
import com.study.security.application.security.jwt.provider.JwtTokenProvider;
import com.study.security.application.security.oauth.cookie.OAuth2CookieProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtCookieProvider jwtCookieProvider;
    private final OAuth2CookieProvider oAuth2CookieProvider;
    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomOAuthUserDetails oAuth2User = (CustomOAuthUserDetails) authentication.getPrincipal();
        Long memberId = oAuth2User.getUid();

        String refreshToken = jwtTokenProvider.generateRefreshToken(memberId);
        jwtCookieProvider.addRefreshTokenCookie(response, refreshToken);

        String redirectUri = determineTargetUrl(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(response);

        getRedirectStrategy().sendRedirect(request, response, redirectUri);

        log.info("OAuth2 로그인 성공 : id - {} / redirect url- {}", memberId, redirectUri);
    }

    private String determineTargetUrl(HttpServletRequest request) {
        return oAuth2CookieProvider.getRedirectUriCookie(request)
                .orElse("http://localhost:3000") + "/oauth/callback";
    }
}
