package org.kwakmunsu.splearn.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kwakmunsu.splearn.domain.MemberFixture.createMemberRegisterRequest;
import static org.kwakmunsu.splearn.domain.MemberFixture.createPasswordEncoder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {

    private Member member;

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        this.passwordEncoder = createPasswordEncoder();

        member = Member.register(createMemberRegisterRequest(), passwordEncoder);
    }

    @DisplayName("멤버를 생성한다")
    @Test
    void registerMember() {
        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
    }

    @DisplayName("회원 상태를 activate로 변경한다.")
    @Test
    void activate() {
        // when
        member.activate();

        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @DisplayName("회원 상태가 가입 대기 상태가 아닌데 가입 완료 시 예외를 던진다.")
    @Test
    void activateFail() {
        // given
        member.activate();

        // when & then
        assertThatThrownBy(member::activate)
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("회원 탈퇴에 성공한다")
    @Test
    void deactivate() {
        // given
        member.activate();

        // when
        member.deactivate();

        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
    }

    @DisplayName("회원 상태가 가입 완료 상태가 아닌데 탈퇴 시 예외를 던진다.")
    @Test
    void deactivateFail() {
        // when & then
        assertThatThrownBy(member::deactivate).isInstanceOf(IllegalStateException.class);

        member.activate();
        member.deactivate();

        assertThatThrownBy(member::deactivate).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("비밀번호 검증")
    @Test
    void verifyPassword() {
        assertThat(member.verifyPassword("secret", passwordEncoder)).isTrue();
        assertThat(member.verifyPassword("hello", passwordEncoder)).isFalse();
    }


    @DisplayName("닉네임을 변경한다")
    @Test
    void changeNickname() {
        assertThat(member.getNickname()).isEqualTo("kkk");

        member.changeNickname("ktp");

        assertThat(member.getNickname()).isEqualTo("ktp");
    }

    @DisplayName("비밀번호를 변경한다")
    @Test
    void changePassword() {
        member.changePassword("updatePassword", passwordEncoder);
        assertThat(member.verifyPassword("updatePassword", passwordEncoder)).isTrue();
    }

    @DisplayName("회원 상태가 Active인지 확인한다")
    @Test
    void isActive() {
        assertThat(member.isActive()).isFalse();
        member.activate();
        assertThat(member.isActive()).isTrue();
        member.deactivate();
        assertThat(member.isActive()).isFalse();
    }

    @DisplayName("이메일 형식이 맞지 않으면 예외를 던진다.")
    @Test
    void invalidEmail() {
        // given
        assertThatThrownBy(() ->
                Member.register(createMemberRegisterRequest("invalid Email"), passwordEncoder)
        ).isInstanceOf(IllegalArgumentException.class);

        Member.register(createMemberRegisterRequest(), passwordEncoder);
    }

}