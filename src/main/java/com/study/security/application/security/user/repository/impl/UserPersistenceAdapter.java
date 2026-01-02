package com.study.security.application.security.user.repository.impl;

import com.study.security.application.security.user.dto.UserAccount;
import com.study.security.application.security.user.repository.UserRepositoryPort;
import com.study.security.domain.member.entity.Member;
import com.study.security.domain.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final MemberRepository memberRepository;

    @Override
    public Optional<UserAccount> findByEmail(String email) {
        return memberRepository.findByEmail(email).map(UserAccount::from);
    }

    @Override
    public Optional<UserAccount> findById(Long id) {
        return memberRepository.findById(id).map(UserAccount::from);
    }

    @Override
    public UserAccount save(UserAccount account) {
        Member member = Member.create(account.email(), account.password(), account.nickname());
        if (StringUtils.hasText(account.profileImageUrl())) {
            member.updateProfileImageUrl(account.profileImageUrl());
        }
        memberRepository.save(member);
        return UserAccount.from(member);
    }

    @Override
    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Override
    public void touchLoginSuccess(Long memberId) {
        memberRepository.findById(memberId).ifPresent(member -> {
            member.recordLoginSuccess();
            memberRepository.save(member);
        });
    }
}
