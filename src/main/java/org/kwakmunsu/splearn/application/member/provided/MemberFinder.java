package org.kwakmunsu.splearn.application.member.provided;

import org.kwakmunsu.splearn.domain.member.Member;
/**
 * 회원을 조회한다.
 * */
public interface MemberFinder {
    Member find(Long memberId);
}