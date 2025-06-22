package org.kwakmunsu.splearn.adapter.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SecurePasswordEncoderTest {

    @DisplayName("패스워드 암호화")
    @Test
    void securePasswordEncoderTest() {
        SecurePasswordEncoder securePasswordEncoder = new SecurePasswordEncoder();

        String passwordHash = securePasswordEncoder.encode("password");

        assertThat(securePasswordEncoder.matches("password", passwordHash)).isTrue();
        assertThat(securePasswordEncoder.matches("wrong", passwordHash)).isFalse();
    }
}