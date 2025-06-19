package org.kwakmunsu.splearn.domain;

import static org.springframework.util.Assert.state;

import java.util.Objects;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Member {

    private String email;

    private String nickname;

    private String passwordHash;

    private MemberStatus status;

    private Member(String email, String nickname, String passwordHash) {
        this.email = Objects.requireNonNull(email);
        this.nickname = Objects.requireNonNull(nickname);
        this.passwordHash = Objects.requireNonNull(passwordHash);
        this.status = MemberStatus.PENDING;
    }

    public static Member create(String email, String nickname, String password, PasswordEncoder passwordEncoder) {
        // 요가서 부가적인 작업이 일어날 수 있음.
        return new Member(email, nickname, passwordEncoder.encode(password));
    }

    public void activate() {
        state(status == MemberStatus.PENDING, "Member is not PENDING.");

        this.status = MemberStatus.ACTIVE;
    }

    public void deactivate() {
        state(status == MemberStatus.ACTIVE, "Member is not ACTIVE.");

        this.status = MemberStatus.DEACTIVATED;
    }

    public boolean verifyPassword(String password, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(password, passwordHash);
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changePassword(String password, PasswordEncoder passwordEncoder) {
        this.passwordHash = passwordEncoder.encode(password);
    }

}

//* state util method
// if 문은 시선을 많이 뻇어 핵심 로직에 집중할 수 없다. 간결하게 작성.
// 해당 메소드가 실행이 되는 시점 혹은 어떤 작업을 하기 전에 반드시 이 상태이어야 하거나 파라미터의 조건을 체크할 떄 많이 사용
// */