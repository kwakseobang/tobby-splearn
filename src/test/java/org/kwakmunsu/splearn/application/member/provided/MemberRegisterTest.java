package org.kwakmunsu.splearn.application.member.provided;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kwakmunsu.splearn.domain.member.MemberStatus.ACTIVE;
import static org.kwakmunsu.splearn.domain.member.MemberStatus.DEACTIVATED;
import static org.kwakmunsu.splearn.domain.member.MemberStatus.PENDING;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.splearn.SplearnTestConfiguration;
import org.kwakmunsu.splearn.domain.member.DuplicateEmailException;
import org.kwakmunsu.splearn.domain.member.DuplicateProfileException;
import org.kwakmunsu.splearn.domain.member.Member;
import org.kwakmunsu.splearn.domain.member.MemberFixture;
import org.kwakmunsu.splearn.domain.member.MemberInfoUpdateRequest;
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
    void activate() {
        // 저장하고 조회할 떄는 flush & clear가 중요함. DB에 쿼리가 날라가는 지 확인.!
        Member member = registerMember();

        member = memberRegister.activate(member.getId());

        entityManager.flush();

        assertThat(member.getStatus()).isEqualTo(ACTIVE);
        assertThat(member.getDetail().getActivatedAt()).isNotNull();
    }

    @DisplayName("deactivate")
    @Test
    void deactivate() {
        Member member = registerMember();

        memberRegister.activate(member.getId());
        entityManager.flush();
        entityManager.clear();

        member = memberRegister.deactivate(member.getId());

        assertThat(member.getStatus()).isEqualTo(DEACTIVATED);
        assertThat(member.getDetail().getDeactivatedAt()).isNotNull();
    }

    private Member registerMember() {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());
        entityManager.flush();
        entityManager.clear();
        return member;
    }

    private Member registerMember(String email) {
        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest(email));
        entityManager.flush();
        entityManager.clear();
        return member;
    }

    @DisplayName("memberInfoUpdate")
    @Test
    void memberInfoUpdate() {
        Member member = registerMember();

        memberRegister.activate(member.getId());
        entityManager.flush();
        entityManager.clear();

        var request = new MemberInfoUpdateRequest("kwakjjrkk", "kwakmunsu", "자기소개");
        member = memberRegister.updateInfo(member.getId(), request);

        assertThat(member.getNickname()).isEqualTo(request.nickname());
        assertThat(member.getDetail().getProfile().address()).isEqualTo(request.profileAddress());
        assertThat(member.getDetail().getIntroduction()).isEqualTo(request.introduction());
    }

    @DisplayName("멤버 정보 업데이트 실패")
    @Test
    void updateInfoFail() {
        Member member = registerMember();
        memberRegister.activate(member.getId());
        memberRegister.updateInfo(member.getId(), new MemberInfoUpdateRequest("kwakjjrkk", "kwakmunsu", "자기소개"));

        Member member2 = registerMember("iikw@gmail.com");
        memberRegister.activate(member2.getId());

        entityManager.flush();
        entityManager.clear();

        // 타 회원과 같은 프로필 주소를 사용할 수 없다.
        assertThatThrownBy(
                () -> memberRegister.updateInfo(member2.getId(), new MemberInfoUpdateRequest("chatgpt", "kwakmunsu", "자기소개"))
        ).isInstanceOf(DuplicateProfileException.class);

        // 다른 프로필 주소로는 변경 가능
        memberRegister.updateInfo(member2.getId(), new MemberInfoUpdateRequest("chatgpt", "kwakmunssddu", "자기소개"));
        // 기존 프로필 주소를 바꾸는 것도 가능
        memberRegister.updateInfo(member.getId(), new MemberInfoUpdateRequest("chatgpt", "kwakmunsu", "자기소개"));
        // 프로필 주소를 제거하는 것도 가능
        memberRegister.updateInfo(member.getId(), new MemberInfoUpdateRequest("chatgpt", "", "자기소개"));
        // 프로필 주소 중복은 허용되지 않음
        assertThatThrownBy(
                () -> memberRegister.updateInfo(member.getId(), new MemberInfoUpdateRequest("chatgpt", "kwakmunssddu", "자기소개"))
        ).isInstanceOf(DuplicateProfileException.class);

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