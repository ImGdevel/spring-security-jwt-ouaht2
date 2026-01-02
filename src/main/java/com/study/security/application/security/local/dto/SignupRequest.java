package com.study.security.application.security.local.dto;

import static com.study.security.common.validation.ValidationMessages.INVALID_EMAIL_FORMAT;
import static com.study.security.common.validation.ValidationMessages.REQUIRED_FIELD;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "회원가입 요청 DTO")
public record SignupRequest(

        @Schema(description = "사용자 이메일", example = "devon@email.com")
        @NotBlank(message = REQUIRED_FIELD)
        @Email(message = INVALID_EMAIL_FORMAT)
        String email,

        @Schema(description = "사용자 비밀번호", example = "password1234")
        @NotBlank(message = REQUIRED_FIELD)
        @Size(min = 8, max = 72, message = "비밀번호는 8자 이상이어야 합니다.")
        String password
) {}
