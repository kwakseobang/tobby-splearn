package org.kwakmunsu.splearn.domain;

import static org.assertj.core.api.Assertions.assertThat;

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

}