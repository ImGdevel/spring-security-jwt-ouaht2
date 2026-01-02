package com.study.security.application.member.adapter;

import com.study.security.application.member.MemberAccount;
import com.study.security.application.member.MemberPort;
import com.study.security.domain.member.entity.Member;
import com.study.security.domain.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class MemberPersistenceAdapter implements MemberPort {

    private final MemberRepository memberRepository;

    @Override
    public Optional<MemberAccount> findByEmail(String email) {
        return memberRepository.findByEmail(email).map(MemberAccount::from);
    }

    @Override
    public Optional<MemberAccount> findById(Long id) {
        return memberRepository.findById(id).map(MemberAccount::from);
    }

    @Override
    public MemberAccount save(MemberAccount account) {
        Member member = Member.create(account.email(), account.password(), account.nickname());
        if (StringUtils.hasText(account.profileImage())) {
            member.updateProfileImage(account.profileImage());
        }
        memberRepository.save(member);
        return MemberAccount.from(member);
    }

    @Override
    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    @Override
    public void touchLoginSuccess(Long memberId) {
        memberRepository.findById(memberId).ifPresent(member -> {
            member.loginSuccess();
            memberRepository.save(member);
        });
    }
}
