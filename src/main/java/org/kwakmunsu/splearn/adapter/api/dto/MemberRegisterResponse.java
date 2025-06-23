package org.kwakmunsu.splearn.adapter.api.dto;

import org.kwakmunsu.splearn.domain.member.Member;

public record MemberRegisterResponse(Long memberId, String emailAddress) {

    public static MemberRegisterResponse of(Member member) {
        return new  MemberRegisterResponse(member.getId(), member.getEmail().address());
    }

}