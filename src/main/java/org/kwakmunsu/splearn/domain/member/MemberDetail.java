package org.kwakmunsu.splearn.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.kwakmunsu.splearn.domain.BaseEntity;
import org.springframework.util.Assert;

@Table(uniqueConstraints =
@UniqueConstraint(name = "UK_MEMBER_DETAIL_PROFILE_ADDRESS", columnNames = "profile_address"))
@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MemberDetail extends BaseEntity {

    @Embedded
    private Profile profile;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    @Column(nullable = false)
    private LocalDateTime registeredAt;

    private LocalDateTime activatedAt;

    private LocalDateTime deactivatedAt;

    static MemberDetail create() {
        MemberDetail detail = new MemberDetail();
        detail.registeredAt = LocalDateTime.now();
        return detail;
    }

    void activate() {
        Assert.isTrue(activatedAt == null, "Activated at is already set");

        this.activatedAt = LocalDateTime.now();
    }

    void deactivate() {
        Assert.isTrue(deactivatedAt == null, "deactivatedAt at is already set");

        this.deactivatedAt = LocalDateTime.now();
    }

    void updateInfo(MemberInfoUpdateRequest request) {
        this.profile = new Profile(request.profileAddress());
        this.introduction = Objects.requireNonNull(request.introduction());
    }

    boolean isProfileEquals(String profileAddress) {
        return profileAddress.equals(this.profile.address());
    }

}