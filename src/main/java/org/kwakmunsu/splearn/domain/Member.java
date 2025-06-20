package org.kwakmunsu.splearn.domain;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.Assert.state;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Member {

    private String email;

    private String nickname;

    private String passwordHash;

    private MemberStatus status;

    // 오브젝트 파라미터 방식을 채택. -> 파라미터가 많을 경우
    // 1. 내부에서 생성자로 만들면 파라미터 타입이 겹치거나 할 경우 순서가 꼬일 수도 있고 헷갈릴 수 있음
    // 2. 빌더패턴은 값을 넣지 않으면 Null값 또는 0 등의 값이 들어가 빌드가 된다. 그럼 런타임 중에 버그가 일어날수있기에 신중해야함.
    // 3. 따라서 필드에 직접 주입함.
    public static Member create(MemberCreateDomainRequest request, PasswordEncoder passwordEncoder) {
        Member member = new Member();

        member.email = requireNonNull(request.email());
        member.nickname = requireNonNull(request.nickname());
        member.passwordHash = requireNonNull(passwordEncoder.encode(request.password()));

        member.status = MemberStatus.PENDING;

        return member;
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
        this.nickname = requireNonNull(nickname);
    }

    public void changePassword(String password, PasswordEncoder passwordEncoder) {
        this.passwordHash = passwordEncoder.encode(requireNonNull(password));
    }


    public boolean isActive() {
        return this.status == MemberStatus.ACTIVE;
    }
}

//* state util method
// if 문은 시선을 많이 뻇어 핵심 로직에 집중할 수 없다. 간결하게 작성.
// 해당 메소드가 실행이 되는 시점 혹은 어떤 작업을 하기 전에 반드시 이 상태이어야 하거나 파라미터의 조건을 체크할 떄 많이 사용
// */