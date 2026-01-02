package com.study.security.application.security.user.validator;

import com.study.security.application.security.local.dto.SignupRequest;
import com.study.security.application.security.user.repository.UserRepositoryPort;
import com.study.security.common.exception.BusinessException;
import com.study.security.common.exception.code.AuthErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthValidator {

    private final UserRepositoryPort userRepositoryPort;

    public void validateSignup(SignupRequest request) {
        if (userRepositoryPort.existsByEmail(request.email())) {
            throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }
}
