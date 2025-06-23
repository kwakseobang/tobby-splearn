package org.kwakmunsu.splearn;

import org.kwakmunsu.splearn.application.member.required.EmailSender;
import org.kwakmunsu.splearn.domain.member.MemberFixture;
import org.kwakmunsu.splearn.domain.member.PasswordEncoder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/*
 * 테스트가 동작할 때 필요로 하는 오브젝트가 구현이 안되었거나 너무 오래 걸릴 때 테스트용 빈으로 만든다.
 * 나중에 구현됐을 경우 해당 테스트 구현체들은 실제 구현체들로 대체함
 * */
@TestConfiguration
public class SplearnTestConfiguration {

    @Bean
    public EmailSender emailSender() {
        return (email, subject, body) -> System.out.println("Sending email: " + email);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return MemberFixture.createPasswordEncoder();
    }
}
