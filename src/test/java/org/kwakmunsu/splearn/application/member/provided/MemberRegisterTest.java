package org.kwakmunsu.splearn.application.member.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kwakmunsu.splearn.domain.member.MemberStatus.ACTIVE;
import static org.kwakmunsu.splearn.domain.member.MemberStatus.PENDING;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.splearn.SplearnTestConfiguration;
import org.kwakmunsu.splearn.domain.member.DuplicateEmailException;
import org.kwakmunsu.splearn.domain.member.Member;
import org.kwakmunsu.splearn.domain.member.MemberFixture;
import org.kwakmunsu.splearn.domain.member.MemberRegisterRequest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@Transactional // 테스트 후 DB 변경사항 자동 롤백(테스트 간 데이터 분리)
@Import(SplearnTestConfiguration.class)
@SpringBootTest
record MemberRegisterTest(MemberRegister memberRegister, EntityManager entityManager) {

    @DisplayName("회원 등록")
    @Test
    void register() {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());

        System.out.println(member);

        assertThat(member.getId()).isNotNull();
        assertThat(member.getStatus()).isEqualTo(PENDING);
    }

    @DisplayName("이메일 중복 테스트")
    @Test
    void duplicateEmailFail() {
        memberRegister.register(MemberFixture.createMemberRegisterRequest());

        assertThatThrownBy(() -> memberRegister.register(MemberFixture.createMemberRegisterRequest()))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @DisplayName("activate")
    @Test
    void  activate() {
        // 저장하고 조회할 떄는 flush & clear가 중요함. DB에 쿼리가 날라가는 지 확인.!
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());
        entityManager.flush();
        entityManager.clear();

        member = memberRegister.activate(member.getId());

        entityManager.flush();

        assertThat(member.getStatus()).isEqualTo(ACTIVE);
    }

    @DisplayName("멤버 request 실패")
    @Test
    void memberRegisterRequestFail() {
        checkValidation(new MemberRegisterRequest("iii1483@gmail.com", "kkk", "longsecret"));
        checkValidation(new MemberRegisterRequest("iii1483@gmail.com", "kkk123_________________________", "longsecret"));
        checkValidation(new MemberRegisterRequest("iii1483gmail.com", "kkk", "longsecret"));
    }

    private void checkValidation(MemberRegisterRequest request) {
        assertThatThrownBy(() -> memberRegister.register(request))
            .isInstanceOf(ConstraintViolationException.class);
    }

}