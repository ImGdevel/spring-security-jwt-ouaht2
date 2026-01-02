package com.study.security.application.security.local.service;

import com.study.security.application.security.user.dto.UserAccount;
import com.study.security.application.security.user.repository.UserRepositoryPort;
import com.study.security.application.security.local.dto.SignupRequest;
import com.study.security.application.security.user.validator.AuthValidator;
import com.study.security.application.security.local.dto.LoginResponse;
import com.study.security.application.security.jwt.provider.JwtTokenProvider;
import com.study.security.domain.member.entity.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final UserRepositoryPort userRepositoryPort;
    private final AuthValidator authValidator;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponse signup(SignupRequest request) {
        authValidator.validateSignup(request);

        String email = request.email();
        String password = passwordEncoder.encode(request.password());
        String nickname = request.nickname();

        UserAccount account = new UserAccount(
                null,
                email,
                password,
                nickname,
                request.profileImage(),
                MemberRole.USER,
                true
        );

        UserAccount savedMember = userRepositoryPort.save(account);

        String accessToken = jwtTokenProvider.generateAccessToken(savedMember.id(), savedMember.role().name());

        return new LoginResponse(savedMember.id(), accessToken);
    }
}
