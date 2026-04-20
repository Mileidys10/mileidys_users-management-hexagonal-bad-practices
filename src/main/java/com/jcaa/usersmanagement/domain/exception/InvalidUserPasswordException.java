package com.jcaa.usersmanagement.domain.exception;

public final class InvalidUserPasswordException extends DomainException {

    private InvalidUserPasswordException(final String message) {
        super(message);
    }

    private static final String EMPTY_PASSWORD_MSG = "The user password must not be empty.";
    private static final String SHORT_PASSWORD_MSG = "The user password must have at least %d characters.";



    public static InvalidUserPasswordException becauseValueIsEmpty() {
        return new InvalidUserPasswordException(EMPTY_PASSWORD_MSG);
    }

    public static InvalidUserPasswordException becauseLengthIsTooShort(final int minimumLength) {
        return new InvalidUserPasswordException(
                String.format(SHORT_PASSWORD_MSG, minimumLength));
    }
}