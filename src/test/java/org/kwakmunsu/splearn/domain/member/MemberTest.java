package org.kwakmunsu.splearn.domain.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kwakmunsu.splearn.domain.member.MemberFixture.createMemberRegisterRequest;
import static org.kwakmunsu.splearn.domain.member.MemberFixture.createPasswordEncoder;

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
        assertThat(member.getDetail().getRegisteredAt()).isNotNull();
    }

    @DisplayName("회원 상태를 activate로 변경한다.")
    @Test
    void activate() {

        assertThat(member.getDetail().getActivatedAt()).isNull();
        // when
        member.activate();

        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
        assertThat(member.getDetail().getActivatedAt()).isNotNull();
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
        assertThat(member.getDetail().getDeactivatedAt()).isNull();
        member.activate();

        // when
        member.deactivate();

        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
        assertThat(member.getDetail().getDeactivatedAt()).isNotNull();
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
        assertThat(member.verifyPassword("longsecret", passwordEncoder)).isTrue();
        assertThat(member.verifyPassword("hello", passwordEncoder)).isFalse();
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

    @DisplayName("회원 정보 업데이트")
    @Test
    void updateInfo() {
        member.activate();
        var request = new MemberInfoUpdateRequest("choi", "kwak0220", "자기소개하기");

        member.updateInfo(request);

        assertThat(member.getNickname()).isEqualTo(request.nickname());
        assertThat(member.getDetail().getProfile().address()).isEqualTo(request.profileAddress());
        assertThat(member.getDetail().getIntroduction()).isEqualTo(request.introduction());
    }

    @DisplayName("등록 완료상태애서만 회원 정보를 수정할 수 있다.")
    @Test
    void updateInfoFail() {
        assertThatThrownBy(() -> {
            var request = new MemberInfoUpdateRequest("choddi", "kwak0220", "자기소개하기");
            member.updateInfo(request);
        })
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("프로필 주소가 맞는지 확인한다")
    @Test
    void isProfileEquals() {
        member.activate();
        var request = new MemberInfoUpdateRequest("choi", "kwak0220", "자기소개하기");

        member.updateInfo(request);

        assertThat(member.isProfileEquals(request.profileAddress())).isTrue();
    }

}