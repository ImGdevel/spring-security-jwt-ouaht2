package com.study.security.application.member.validator;

import com.study.security.application.member.dto.request.SignupRequest;
import com.study.security.common.exception.BusinessException;
import com.study.security.common.exception.code.AuthErrorCode;
import com.study.security.application.member.MemberPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthValidator {

    private final MemberPort memberPort;

    public void validateSignup(SignupRequest request) {
        if (memberPort.existsByEmail(request.email())) {
            throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if (memberPort.existsByNickname(request.nickname())) {
            throw new BusinessException(AuthErrorCode.NICKNAME_ALREADY_EXISTS);
        }
    }
}
