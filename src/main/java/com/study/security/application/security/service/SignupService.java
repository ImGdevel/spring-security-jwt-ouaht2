package com.study.security.application.security.service;

import com.study.security.application.member.dto.request.SignupRequest;
import com.study.security.application.member.validator.AuthValidator;
import com.study.security.presentation.dto.response.LoginResponse;
import com.study.security.application.security.util.JwtTokenProvider;
import com.study.security.domain.member.entity.Member;
import com.study.security.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignupService {
    private final MemberRepository memberRepository;
    private final AuthValidator authValidator;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponse signup(SignupRequest request){
        authValidator.validateSignup(request);

        String email = request.email();
        String password = passwordEncoder.encode(request.password());
        String nickname = request.nickname();

        Member member = Member.create(email, password, nickname);
        member.updateProfileImage(request.profileImage());

        Member savedMember = memberRepository.save(member);

        String accessToken = jwtTokenProvider.generateAccessToken(savedMember.getId(), savedMember.getRole().name());
        return new LoginResponse(savedMember.getId(), accessToken);
    }
}
