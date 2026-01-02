package com.study.security.application.security.oauth;

public interface OAuthUserInfo {
    String getId();
    String getProvider();
    String getEmail();
    String getName();
}