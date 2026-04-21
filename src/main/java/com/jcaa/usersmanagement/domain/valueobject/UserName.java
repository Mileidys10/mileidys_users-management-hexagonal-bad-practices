package com.jcaa.usersmanagement.domain.valueobject;

import com.jcaa.usersmanagement.domain.exception.InvalidUserNameException;
import java.util.Objects;


public record UserName(String value) {

    private static final String NULL_NAME_MSG = "UserName cannot be null";
    private static final int MINIMUM_LENGTH = 3;

    public UserName {
        Objects.requireNonNull(value, NULL_NAME_MSG);

        final String normalizedValue = value.trim();

        validateNotEmpty(normalizedValue);
        validateMinimumLength(normalizedValue);

        value = normalizedValue;
    }

    private void validateNotEmpty(final String name) {
        if (name.isEmpty()) {
            throw InvalidUserNameException.becauseValueIsEmpty();
        }
    }

    private void validateMinimumLength(final String name) {
        if (name.length() < MINIMUM_LENGTH) {
            throw InvalidUserNameException.becauseLengthIsTooShort(MINIMUM_LENGTH);
        }
    }

    @Override
    public String toString() {
        return value;
    }
}