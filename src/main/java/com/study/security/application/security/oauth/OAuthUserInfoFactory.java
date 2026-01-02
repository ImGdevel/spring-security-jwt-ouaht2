package com.study.security.application.security.oauth;

import com.study.security.application.security.oauth.provider.GithubUserInfo;
import com.study.security.application.security.oauth.provider.GoogleUserInfo;
import java.util.Map;

public class OAuthUserInfoFactory {
    public static OAuthUserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> new GoogleUserInfo(attributes);
            case "github" -> new GithubUserInfo(attributes);
            default -> throw new IllegalArgumentException("지원하지 않는 OAuth2 Provider: " + registrationId);
        };
    }
}