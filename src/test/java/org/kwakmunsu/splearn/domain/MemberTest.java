package org.kwakmunsu.splearn.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {

    @DisplayName("멤버를 생성한다")
    @Test
    void createMember() {
        // given
        var member = new Member("iii6602@gmail.com", "kkk", "secret");
        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
    }

    @DisplayName("회원 생성시 Null이 들어가면 예외를 던진다.")
    @Test
    void createMemberNull() {
        assertThatThrownBy(() -> new Member(null, "kkk", "secret"))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("회원 상태를 activate로 변경한다.")
    @Test
    void activate() {
        // given
        var member = new Member("iii6602@gmail.com", "kkk", "secret");

        // when
        member.activate();

        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @DisplayName("회원 상태가 가입 대기 상태가 아닌데 가입 완료 시 예외를 던진다.")
    @Test
    void activateFail() {
        // given
        var member = new Member("iii6602@gmail.com", "kkk", "secret");
        member.activate();

        // when & then
        assertThatThrownBy(member::activate)
                .isInstanceOf(IllegalStateException.class);
    }


    @DisplayName("회원 탈퇴에 성공한다")
    @Test
    void deactivate() {
        // given
        var member = new Member("iii6602@gmail.com", "kkk", "secret");
        member.activate();

        // when
        member.deactivate();

        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
    }

    @DisplayName("회원 상태가 가입 완료 상태가 아닌데 탈퇴 시 예외를 던진다.")
    @Test
    void deactivateFail() {
        // given
        var member = new Member("iii6602@gmail.com", "kkk", "secret");

        // when & then
        assertThatThrownBy(member::deactivate).isInstanceOf(IllegalStateException.class);

        member.activate();
        member.deactivate();

        assertThatThrownBy(member::deactivate).isInstanceOf(IllegalStateException.class);
    }

}