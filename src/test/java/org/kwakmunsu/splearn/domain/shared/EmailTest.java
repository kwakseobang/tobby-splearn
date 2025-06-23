package org.kwakmunsu.splearn.domain.shared;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmailTest {

    @DisplayName("Email 동등성 비교")
    @Test
    void  equality() {
        var email1 = new Email("iii1483@gmail.com");
        var email2 = new Email("iii1483@gmail.com");

        assertThat(email1).isEqualTo(email2);
    }
}