package org.kwakmunsu.splearn.application.required;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kwakmunsu.splearn.domain.MemberFixture.createMemberRegisterRequest;
import static org.kwakmunsu.splearn.domain.MemberFixture.createPasswordEncoder;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.splearn.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
        /*
        * save()했다고 정말 save된 것이 아닐 수도 있다. DB에 flush 되지 않았을 수도 있음 따라서 DB에 잘 들어갔는지 sql이 잘 만들어졌는지 확인할려면
        *  Jpa entity를 관리하는 즉 Persistence Context를 관리하는 EntityManager를 가져와서 flush를 해줘야 DB의 변경 사항을 체크 할 수 있다.
        * flush만 사용할경우는 저장을 확인할 수 있다. clear까지 사용하면 조회 가능
        * JPA를 사용하는 작업에서는 한번씩 해줘야 하는 작업이다.
        **/
        entityManager.flush();
    }

}