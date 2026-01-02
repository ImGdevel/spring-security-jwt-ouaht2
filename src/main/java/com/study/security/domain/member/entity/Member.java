package com.study.security.domain.member.entity;

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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 64)
    private String nickname;

    @Column(length = 512)
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    private Instant lastLoginAt;

    private Member(String email, String password, String nickname, String profileImage,
                   MemberRole role, MemberStatus status, Instant createdAt, Instant updatedAt, Instant lastLoginAt) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastLoginAt = lastLoginAt;
    }

    public static Member create(String email, String password, String nickname) {
        Instant now = Instant.now();
        return new Member(email, password, nickname, null,
                MemberRole.USER, MemberStatus.ACTIVE, now, now, null);
    }

    public void updateProfileImage(String profileImage) {
        if (profileImage == null || profileImage.isBlank()) {
            return;
        }
        this.profileImage = profileImage;
        this.updatedAt = Instant.now();
    }

    public void loginSuccess() {
        this.lastLoginAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public boolean isActive() {
        return status == MemberStatus.ACTIVE;
    }
}
