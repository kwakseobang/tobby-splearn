package org.kwakmunsu.splearn.domain;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.Assert.state;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

@Table(name = "MEMBER", uniqueConstraints =
@UniqueConstraint(name = "UK_MEMBER_EMAIL_ADDRESS", columnNames = "email_address"))
@NaturalIdCache // NaturalId 적용한 필드들이 캐싱이 된다.
@ToString(callSuper = true) // 부모의 값도 같이 출력
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseEntity {

    // 비즈니스적으로 의미 있는 고유값(자연키, natural key)**을 나타내는 필드에 붙인다. JPA가 아닌 hibernate가 지원함
    // 성능이 향상 됨. 캐싱 지원
    @NaturalId
    @Embedded
    private Email email;

    @Column(length = 100, nullable = false)
    private String nickname;

    @Column(length = 200, nullable = false)
    private String passwordHash;

    @Column(length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    // 오브젝트 파라미터 방식을 채택. -> 파라미터가 많을 경우
    // 1. 내부에서 생성자로 만들면 파라미터 타입이 겹치거나 할 경우 순서가 꼬일 수도 있고 헷갈릴 수 있음
    // 2. 빌더패턴은 값을 넣지 않으면 Null값 또는 0 등의 값이 들어가 빌드가 된다. 그럼 런타임 중에 버그가 일어날수있기에 신중해야함.
    // 3. 따라서 필드에 직접 주입함.
    public static Member register(MemberRegisterRequest request, PasswordEncoder passwordEncoder) {
        Member member = new Member();

        member.email = new Email(request.email());
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