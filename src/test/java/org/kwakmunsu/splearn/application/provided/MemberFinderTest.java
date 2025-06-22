package org.kwakmunsu.splearn.application.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.splearn.SplearnTestConfiguration;
import org.kwakmunsu.splearn.domain.Member;
import org.kwakmunsu.splearn.domain.MemberFixture;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@Transactional // 테스트 후 DB 변경사항 자동 롤백(테스트 간 데이터 분리)
@Import(SplearnTestConfiguration.class)
@SpringBootTest
record MemberFinderTest(MemberFinder memberFinder, MemberRegister memberRegister, EntityManager entityManager) {

    @DisplayName("멤버를 조회한다")
    @Test
    void find() {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());
        entityManager.flush();
        entityManager.clear();

        Member found = memberFinder.find(member.getId());

        assertThat(found.getId()).isEqualTo(member.getId());
    }

    @DisplayName("멤버 조회 실패")
    @Test
    void findFail() {
        assertThatThrownBy(() -> memberFinder.find(999L))
            .isInstanceOf(IllegalArgumentException.class);
    }

}