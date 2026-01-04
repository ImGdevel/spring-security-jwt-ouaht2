package com.study.security.application.security.common.constants;

public class SecurityConstants {

    public static final String LOGIN_URL = "/auth/login";
    public static final String LOGOUT_URL = "/auth/logout";
    public static final String SIGNUP_URL = "/auth/signup";
    public static final String REFRESH_TOKEN_URL = "/auth/refresh";
    public static final String AUTH_COOKIE_PATH = "/auth";
    public static final String OAUTH_URL = "/oauth2/**";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    public static final String[] PUBLIC_URLS = {
            "/",
            LOGIN_URL, LOGOUT_URL, SIGNUP_URL, REFRESH_TOKEN_URL,
            OAUTH_URL, "/api/*",
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**", "/actuator/**"
    };

    public static final String[] SECURE_URLS = {
            "/api/*/secure/**", "/api/*/user/**"
    };

    public static final String[] ADMIN_URLS = {
            "/api/*/admin/**", "/admin/**"
    };
}
