package com.jcaa.usersmanagement.domain.valueobject;

import com.jcaa.usersmanagement.domain.exception.InvalidUserEmailException;
import java.util.Objects;
import java.util.regex.Pattern;


public record UserEmail(String value) {

    private static final String NULL_EMAIL_MSG = "UserEmail cannot be null";

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");

    public UserEmail {
        Objects.requireNonNull(value, NULL_EMAIL_MSG);

        final String normalizedValue = value.trim().toLowerCase();


        validateNotEmpty(normalizedValue);
        validateFormat(normalizedValue);

        value = normalizedValue;
    }

    private void validateNotEmpty(final String email) {
        if (email.isEmpty()) {
            throw InvalidUserEmailException.becauseValueIsEmpty();
        }
    }

    private void validateFormat(final String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw InvalidUserEmailException.becauseFormatIsInvalid(email);
        }
    }

    @Override
    public String toString() {
        return value;
    }
}