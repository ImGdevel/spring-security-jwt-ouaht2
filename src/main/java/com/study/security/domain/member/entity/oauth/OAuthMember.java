package com.study.security.domain.member.entity.oauth;

import com.study.security.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "oauth_members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String providerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private Instant createdAt;

    private OAuthMember(String provider, String providerId, Member member, Instant createdAt) {
        this.provider = provider;
        this.providerId = providerId;
        this.member = member;
        this.createdAt = createdAt;
    }

    public static OAuthMember create(String provider, String providerId, Member member) {
        return new OAuthMember(provider, providerId, member, Instant.now());
    }
}
