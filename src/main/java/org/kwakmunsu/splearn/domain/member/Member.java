package org.kwakmunsu.splearn.domain.member;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.Assert.state;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.kwakmunsu.splearn.domain.BaseEntity;
import org.kwakmunsu.splearn.domain.shared.Email;

@Table(name = "MEMBER", uniqueConstraints = {
        @UniqueConstraint(name = "UK_MEMBER_EMAIL_ADDRESS", columnNames = "email_address"),
        @UniqueConstraint(name = "UK_MEMBER_DETAIL_ID", columnNames = "detail_id")
}
)
@NaturalIdCache
@ToString(callSuper = true, exclude = "detail")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseEntity {

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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private MemberDetail detail;

    public static Member register(MemberRegisterRequest request, PasswordEncoder passwordEncoder) {
        Member member = new Member();

        member.email = new Email(request.email());
        member.nickname = requireNonNull(request.nickname());
        member.passwordHash = requireNonNull(passwordEncoder.encode(request.password()));
        member.status = MemberStatus.PENDING;
        member.detail = MemberDetail.create();

        return member;
    }

    public void activate() {
        state(status == MemberStatus.PENDING, "Member is not PENDING.");

        this.status = MemberStatus.ACTIVE;
        this.detail.activate();
    }

    public void deactivate() {
        state(status == MemberStatus.ACTIVE, "Member is not ACTIVE.");

        this.status = MemberStatus.DEACTIVATED;
        this.detail.deactivate();
    }

    public boolean verifyPassword(String password, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(password, passwordHash);
    }

    public void changeNickname(String nickname) {
        this.nickname = requireNonNull(nickname);
    }

    public void updateInfo(MemberInfoUpdateRequest request) {
        this.nickname = Objects.requireNonNull(request.nickname());
        this.detail.updateInfo(request);
    }

    public void changePassword(String password, PasswordEncoder passwordEncoder) {
        this.passwordHash = passwordEncoder.encode(requireNonNull(password));
    }

    public boolean isActive() {
        return this.status == MemberStatus.ACTIVE;
    }

}