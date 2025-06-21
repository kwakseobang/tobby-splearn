package org.kwakmunsu.splearn.application;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.splearn.application.provided.MemberRegister;
import org.kwakmunsu.splearn.application.required.EmailSender;
import org.kwakmunsu.splearn.application.required.MemberRepository;
import org.kwakmunsu.splearn.domain.Member;
import org.kwakmunsu.splearn.domain.MemberRegisterRequest;
import org.kwakmunsu.splearn.domain.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService implements MemberRegister {

    private final MemberRepository memberRepository;
    private final EmailSender emailSender;
    private final PasswordEncoder passwordEncoder;

    /*
     * 애플리케이션 서비스 코드를 만들 때 중요하게 보는 것은
     * 도메인은 도메인 모델을 잘 반영해서 코드를 만들어 내고 애플리케이션 서비스는 실제로 우리 서비스에서 사용하는 주요한 어떤 작업들을 어떤 절차를 거쳐서
     * 이 기능을 수행하는가 누구와 협력을 하는 지 코드 안에서 잘 읽히도록 작성하는 걸 본다.
     * */
    @Override
    public Member register(MemberRegisterRequest request) {
        // check

        // domain model
        Member member = Member.register(request, passwordEncoder);
        // repository
        memberRepository.save(member);
        // post process
        emailSender.send(member.getEmail(), "등록을 완료해주세요.", "아래 링크를 클릭해서 등록을 완료해주세요.");

        return member;
    }

}