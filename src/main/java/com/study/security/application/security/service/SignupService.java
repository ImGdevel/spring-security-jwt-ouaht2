package com.study.security.application.security.service;

import com.study.security.application.member.MemberAccount;
import com.study.security.application.member.MemberPort;
import com.study.security.application.member.dto.request.SignupRequest;
import com.study.security.application.member.validator.AuthValidator;
import com.study.security.presentation.dto.response.LoginResponse;
import com.study.security.application.security.jwt.JwtTokenProvider;
import com.study.security.domain.member.entity.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final MemberPort memberPort;
    private final AuthValidator authValidator;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponse signup(SignupRequest request) {
        authValidator.validateSignup(request);

        String email = request.email();
        String password = passwordEncoder.encode(request.password());
        String nickname = request.nickname();

        MemberAccount account = new MemberAccount(
                null,
                email,
                password,
                nickname,
                request.profileImage(),
                MemberRole.USER,
                true
        );

        MemberAccount savedMember = memberPort.save(account);

        String accessToken = jwtTokenProvider.generateAccessToken(savedMember.id(), savedMember.role().name());

        return new LoginResponse(savedMember.id(), accessToken);
    }
}
