package com.jcaa.usersmanagement.domain.valueobject;

import com.jcaa.usersmanagement.domain.exception.InvalidUserIdException;
import java.util.Objects;


public record UserId(String value) {

    private static final String NULL_ID_MSG = "UserId cannot be null";

    public UserId {
        Objects.requireNonNull(value, NULL_ID_MSG);

        final String normalizedValue = value.trim();

        validateNotEmpty(normalizedValue);

        value = normalizedValue;
    }


    private void validateNotEmpty(final String id) {
        if (id.isEmpty()) {
            throw InvalidUserIdException.becauseValueIsEmpty();
        }
    }

    @Override
    public String toString() {
        return value;
    }
}