package com.study.security.application.member;

import com.study.security.domain.member.entity.Member;
import com.study.security.domain.member.entity.MemberRole;

/**
 * 애플리케이션-레벨에서 사용하는 회원 정보 DTO.
 * <p>Member 엔티티 변경 영향범위를 축소하기 위해 Member 도메인 의존을 이 레이어에서만 캡슐화합니다.</p>
 *
 * @param id 등록된 사용자 ID
 * @param email 이메일
 * @param password 암호화된 비밀번호
 * @param nickname 닉네임
 * @param role 회원 역할(enum)
 * @param active 활성화 여부
 */
public record MemberAccount(
        Long id,
        String email,
        String password,
        String nickname,
        String profileImage,
        MemberRole role,
        boolean active
) {

    public static MemberAccount from(Member member) {
        return new MemberAccount(
                member.getId(),
                member.getEmail(),
                member.getPassword(),
                member.getNickname(),
                member.getProfileImage(),
                member.getRole(),
                member.isActive()
        );
    }

    public Member toEntity() {
        Member member = Member.create(email, password, nickname);
        if (role != MemberRole.USER) {
            // Member.create 기본 role이 USER이므로 필요시 조정
        }
        if (profileImage != null && !profileImage.isBlank()) {
            member.updateProfileImage(profileImage);
        }
        return member;
    }
}
