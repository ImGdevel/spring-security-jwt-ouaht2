package com.study.security.application.security.user.repository;

import com.study.security.application.security.user.dto.UserAccount;
import java.util.Optional;

public interface UserRepositoryPort {

    Optional<UserAccount> findByEmail(String email);

    Optional<UserAccount> findById(Long id);

    UserAccount save(UserAccount account);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    void touchLoginSuccess(Long memberId);
}
