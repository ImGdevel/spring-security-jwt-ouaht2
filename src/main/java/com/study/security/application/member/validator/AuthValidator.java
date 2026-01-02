package com.study.security.application.member.validator;

import com.study.security.application.member.dto.request.SignupRequest;
import com.study.security.common.exception.BusinessException;
import com.study.security.common.exception.code.AuthErrorCode;
import com.study.security.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthValidator {

    private final MemberRepository memberRepository;

    public void validateSignup(SignupRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if (memberRepository.existsByNickname(request.nickname())) {
            throw new BusinessException(AuthErrorCode.NICKNAME_ALREADY_EXISTS);
        }
    }
}
