package org.kwakmunsu.splearn.application.required;

import org.kwakmunsu.splearn.domain.Member;
import org.springframework.data.repository.Repository;

/**
 * Repository는 마커 인터페이스라 저 안에 구현된 메서드들이 없다. 그냥 명시용이다.
 * 회원 정보를 저장하거나 조회한다.
 */
public interface MemberRepository extends Repository<Member, Long> {

    Member save(Member member);

}