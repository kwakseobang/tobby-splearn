package org.kwakmunsu.splearn.domain.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProfileTest {

    @DisplayName("프로필 주소 유효성 검증 성공")
    @Test
    void verifyProfile() {
        new  Profile("kwakmunsu");
        new  Profile("kwakmu22");
        new  Profile("1234");
        new  Profile("");
    }

    @DisplayName("프로필 주소 유효성 검증 실패")
    @Test
    void verifyProfileFail() {
        assertThatThrownBy(() -> new Profile("toLongglognognognogngnog")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Profile("A")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Profile("프로필")).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("url 생성")
    @Test
    void url() {
        var profile = new Profile("kwakmunsu");

        assertThat(profile.url()).isEqualTo("@kwakmunsu");
    }

}