package com.study.security.domain.member.entity;

import com.study.security.domain.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", length = 50, nullable = false)
    private String nickname;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemberStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private MemberRole role;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "agreed_terms_at")
    private Instant agreedTermsAt;

    @Column(name = "agreed_privacy_at")
    private Instant agreedPrivacyAt;

    public static Member create(String email, String password, String nickname) {
        validateCreate(email, password, nickname);
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .status(MemberStatus.ACTIVE)
                .role(MemberRole.USER)
                .build();
    }

    public void changeNickname(String nickname) {
        validateNickname(nickname);
        this.nickname = nickname;
        touchUpdated();
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        if (profileImageUrl != null && profileImageUrl.length() > 500) {
            throw new IllegalArgumentException("profileImageUrl too long");
        }
        this.profileImageUrl = profileImageUrl;
        touchUpdated();
    }

    public void recordLoginSuccess() {
        this.lastLoginAt = Instant.now();
        touchUpdated();
    }

    public void markTermsAgreed(Instant when) {
        this.agreedTermsAt = when;
        touchUpdated();
    }

    public void markPrivacyAgreed(Instant when) {
        this.agreedPrivacyAt = when;
        touchUpdated();
    }

    public void block() {
        this.status = MemberStatus.BLOCKED;
        touchUpdated();
    }

    public void withdraw() {
        this.status = MemberStatus.DELETED;
        touchUpdated();
    }

    public boolean isActive() {
        return status == MemberStatus.ACTIVE;
    }

    private static void validateCreate(String email, String password, String nickname) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email required");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password required");
        }
        validateNickname(nickname);
    }

    private static void validateNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("nickname required");
        }
        if (nickname.length() > 50) {
            throw new IllegalArgumentException("nickname too long");
        }
    }
}
