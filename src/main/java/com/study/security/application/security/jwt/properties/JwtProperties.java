package com.study.security.application.security.jwt.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Component
@Validated
@ConfigurationProperties(prefix = "spring.security.jwt")
public class JwtProperties {

    @NotBlank
    private String secret;

    @NotNull
    @Positive
    private Long accessTokenExpiration;

    @NotNull
    @Positive
    private Long refreshTokenExpiration;
}
