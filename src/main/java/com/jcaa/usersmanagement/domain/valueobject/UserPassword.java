package com.jcaa.usersmanagement.domain.valueobject;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.jcaa.usersmanagement.domain.exception.InvalidUserPasswordException;
import java.util.Objects;

public final class UserPassword {

    private static final int MINIMUM_LENGTH = 8;
    private static final int BCRYPT_COST = 12;
    private static final String NULL_PASSWORD_MSG = "Password cannot be null";
    private static final String NULL_HASH_MSG = "Password hash cannot be null";
    private static final String NULL_PLAIN_MSG = "Plain password cannot be null";

    private final String value;

    private UserPassword(final String value) {
        this.value = value;
    }

    public static UserPassword fromPlainText(final String plainText) {
        Objects.requireNonNull(plainText, NULL_PASSWORD_MSG);

        final String normalizedValue = plainText.trim();

        validateNotEmpty(normalizedValue);
        validateMinimumLength(normalizedValue);

        final String hash = BCrypt.withDefaults()
                .hashToString(BCRYPT_COST, normalizedValue.toCharArray());

        return new UserPassword(hash);
    }


    public static UserPassword fromHash(final String hash) {
        Objects.requireNonNull(hash, NULL_HASH_MSG);
        return new UserPassword(hash);
    }

    public boolean verifyPlain(final String plainText) {
        final String normalizedPlain = Objects.requireNonNull(plainText, NULL_PLAIN_MSG).trim();
        final BCrypt.Result result = BCrypt.verifyer()
                .verify(normalizedPlain.toCharArray(), this.value);
        return result.verified;
    }

    private static void validateNotEmpty(final String password) {
        if (password.isEmpty()) {
            throw InvalidUserPasswordException.becauseValueIsEmpty();
        }
    }

    private static void validateMinimumLength(final String password) {
        if (password.length() < MINIMUM_LENGTH) {
            throw InvalidUserPasswordException.becauseLengthIsTooShort(MINIMUM_LENGTH);
        }
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPassword that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}