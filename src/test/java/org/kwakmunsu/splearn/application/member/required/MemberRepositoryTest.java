package org.kwakmunsu.splearn.application.member.required;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kwakmunsu.splearn.domain.member.MemberFixture.createMemberRegisterRequest;
import static org.kwakmunsu.splearn.domain.member.MemberFixture.createPasswordEncoder;
import static org.kwakmunsu.splearn.domain.member.MemberStatus.PENDING;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.splearn.domain.member.Member;
import org.kwakmunsu.splearn.domain.member.MemberStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * 모든 빈을 다띄우지 않고 DataJpa repository가 동작하는데 필요로 하는 최소한의 Bean만 스프링 컨테이너에 띄운다.
 */
@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager entityManager;

    @DisplayName("멤버 생성")
    @Test
    void createMember() {
        // given
        Member member = Member.register(createMemberRegisterRequest(), createPasswordEncoder());

        assertThat(member.getId()).isNull();
        // when
        memberRepository.save(member); // 이후 영속화

        assertThat(member.getId()).isNotNull();

        entityManager.flush();
        entityManager.clear();

        var found = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(found.getStatus()).isEqualTo(PENDING);
        assertThat(found.getDetail().getRegisteredAt()).isNotNull();
    }


    @DisplayName("중복 이메일이 들어가면 실패한다")
    @Test
    void duplicateEmail() {
        // given
        Member member1 = Member.register(createMemberRegisterRequest(), createPasswordEncoder());
        memberRepository.save(member1);
        Member member2 = Member.register(createMemberRegisterRequest(), createPasswordEncoder());
        // when & then
        assertThatThrownBy(() -> memberRepository.save(member2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

}