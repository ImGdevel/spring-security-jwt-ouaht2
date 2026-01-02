package com.study.security.application.member;

import java.util.Optional;

public interface MemberPort {

    Optional<MemberAccount> findByEmail(String email);

    Optional<MemberAccount> findById(Long id);

    MemberAccount save(MemberAccount account);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    void touchLoginSuccess(Long memberId);
}
