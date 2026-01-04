package com.study.security.application.security.local.config;

import com.study.security.application.security.common.constants.SecurityConstants;
import com.study.security.application.security.local.filter.CustomLoginAuthenticationFilter;
import com.study.security.application.security.local.filter.CustomLogoutFilter;
import com.study.security.application.security.local.handler.LoginFailureHandler;
import com.study.security.application.security.local.handler.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 로컬 로그인/로그아웃 관련 Spring Security 구성을 담당한다.
 */
@Configuration
@RequiredArgsConstructor
public class LocalSecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final CustomLogoutFilter customLogoutFilter;

    public void configure(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

        CustomLoginAuthenticationFilter loginFilter = new CustomLoginAuthenticationFilter(
                authenticationManager, loginSuccessHandler, loginFailureHandler);
        loginFilter.setFilterProcessesUrl(SecurityConstants.LOGIN_URL);

        http
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(customLogoutFilter, UsernamePasswordAuthenticationFilter.class);
    }
}

