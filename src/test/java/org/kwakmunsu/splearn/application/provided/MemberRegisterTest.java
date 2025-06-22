package org.kwakmunsu.splearn.application.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kwakmunsu.splearn.domain.MemberStatus.PENDING;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.splearn.SplearnTestConfiguration;
import org.kwakmunsu.splearn.domain.DuplicateEmailException;
import org.kwakmunsu.splearn.domain.Member;
import org.kwakmunsu.splearn.domain.MemberFixture;
import org.kwakmunsu.splearn.domain.MemberRegisterRequest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@Transactional // 테스트 후 DB 변경사항 자동 롤백(테스트 간 데이터 분리)
@Import(SplearnTestConfiguration.class)
@SpringBootTest
public record MemberRegisterTest(MemberRegister memberRegister) {

    @DisplayName("회원 등록")
    @Test
    void register() {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());

        assertThat(member.getId()).isNotNull();
        assertThat(member.getStatus()).isEqualTo(PENDING);
    }

    @DisplayName("이메일 중복 테스트")
    @Test
    void duplicateEmailFail() {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());

        assertThatThrownBy(() -> memberRegister.register(MemberFixture.createMemberRegisterRequest()))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @DisplayName("멤버 request 실패")
    @Test
    void memberRegisterRequestFail() {
        extracted(new MemberRegisterRequest("iii1483@gmail.com", "kkk", "longsecret"));
        extracted(new MemberRegisterRequest("iii1483@gmail.com", "kkk123_________________________", "longsecret"));
        extracted(new MemberRegisterRequest("iii1483gmail.com", "kkk", "longsecret"));
    }

    private void extracted(MemberRegisterRequest request) {
        assertThatThrownBy(() -> memberRegister.register(request))
            .isInstanceOf(ConstraintViolationException.class);
    }

}