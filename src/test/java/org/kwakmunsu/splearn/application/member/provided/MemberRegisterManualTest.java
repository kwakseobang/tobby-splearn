//package org.kwakmunsu.splearn.application.member.provided;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.kwakmunsu.splearn.application.MemberService;
//import org.kwakmunsu.splearn.application.member.required.EmailSender;
//import org.kwakmunsu.splearn.application.member.required.MemberRepository;
//import org.kwakmunsu.splearn.domain.shared.Email;
//import org.kwakmunsu.splearn.domain.member.Member;
//import org.kwakmunsu.splearn.domain.member.MemberFixture;
//import org.kwakmunsu.splearn.domain.member.MemberStatus;
//import org.mockito.Mockito;
//import org.springframework.test.util.ReflectionTestUtils;
//
//class MemberRegisterManualTest {
//
//    @DisplayName("멤버 등록 stub 테스트")
//    @Test
//    void registerTestStub() {
//        // given
//        MemberRegister memberRegister = new MemberService(
//                new MemberRepositoryStub(), new EmailSenderStub(), MemberFixture.createPasswordEncoder()
//        );
//        // when
//        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());
//        // then
//        assertThat(member.getId()).isEqualTo(1L);
//        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
//    }
//
//    @DisplayName("멤버 등록 mock 테스트")
//    @Test
//    void registerTestMock() {
//        // given
//        EmailSenderMock emailSenderMock = new EmailSenderMock();
//        MemberRegister memberRegister = new MemberService(
//                new MemberRepositoryStub(), emailSenderMock, MemberFixture.createPasswordEncoder()
//        );
//        // when
//        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());
//        // then
//        assertThat(member.getId()).isEqualTo(1L);
//        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
//
//        assertThat(emailSenderMock.emails.size()).isEqualTo(1);
//        assertThat(emailSenderMock.emails.getFirst()).isEqualTo(member.getEmail());
//    }
//
//    @DisplayName("멤버 등록 mockito 테스트")
//    @Test
//    void registerTestMockito() {
//        // given
//        EmailSender emailSenderMock = Mockito.mock(EmailSender.class);
//        MemberRegister memberRegister = new MemberService(
//                new MemberRepositoryStub(), emailSenderMock, MemberFixture.createPasswordEncoder()
//        );
//        // when
//        Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());
//        // then
//        assertThat(member.getId()).isEqualTo(1L);
//        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
//
//        Mockito.verify(emailSenderMock).send(eq(member.getEmail()), any(), any());
//    }
//
//    static class MemberRepositoryStub implements MemberRepository {
//
//        @Override
//        public Optional<Member> findByEmail(Email email) {
//            return Optional.empty();
//        }
//
//        @Override
//        public Member save(Member member) {
//            ReflectionTestUtils.setField(member, "id", 1L);
//            return member;
//        }
//    }
//
//    static class EmailSenderStub implements EmailSender {
//
//        @Override
//        public void send(Email email, String subject, String body) {
//        }
//    }
//
//    static class EmailSenderMock implements EmailSender {
//        List<Email> emails = new ArrayList<>();
//
//        @Override
//        public void send(Email email, String subject, String body) {
//            emails.add(email);
//        }
//    }
//
//}