package org.kwakmunsu.splearn.domain.shared;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;

@Embeddable
public record Email(@Column(name = "email_address", length = 150, nullable = false) String address) {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,7}$");

    public Email {
        if (!EMAIL_PATTERN.matcher(address).matches()) {
            throw new IllegalArgumentException("이메일 형식이 옳바르지 않습니다." + address);
        }
    }

}