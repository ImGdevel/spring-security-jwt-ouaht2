package com.study.security.application.security.config;

import com.study.security.application.security.oauth.handler.OAuthLoginSuccessHandler;
import com.study.security.application.security.oauth.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.study.security.application.security.oauth.service.OAuthLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@RequiredArgsConstructor
public class OAuth2SecurityConfig {

    private final OAuthLoginSuccessHandler oAuthLoginSuccessHandler;
    private final OAuthLoginService oAuthLoginService;
    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

    public void configure(HttpSecurity http) throws Exception {
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.userService(oAuthLoginService))
                .authorizationEndpoint(auth -> auth.authorizationRequestRepository(authorizationRequestRepository))
                .successHandler(oAuthLoginSuccessHandler)
        );
    }
}
