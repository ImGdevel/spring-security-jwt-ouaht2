package com.study.security.common.swagger;

public enum SwaggerErrorResponseDescription {

    AUTH_SIGNUP("회원가입 관련 오류"),
    DEFAULT("공통 오류");

    private final String description;

    SwaggerErrorResponseDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
