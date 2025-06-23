package org.kwakmunsu.splearn.application.member;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.splearn.application.member.provided.MemberFinder;
import org.kwakmunsu.splearn.application.member.required.MemberRepository;
import org.kwakmunsu.splearn.domain.member.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
@Service
public class MemberQueryService implements MemberFinder {

    private final MemberRepository memberRepository;

    @Override
    public Member find(Long memberId) {
        // 람다를 사용하면  "필요할 때만 예외 메시지를 생성해서, 불필요한 문자열 결합 비용을 줄임.
        return memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("Member not found"));
    }

}