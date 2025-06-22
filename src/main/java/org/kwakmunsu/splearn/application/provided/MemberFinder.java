package org.kwakmunsu.splearn.application.provided;

import org.kwakmunsu.splearn.domain.Member;
/**
 * 회원을 조회한다.
 * */
public interface MemberFinder {
    Member find(Long memberId);
}