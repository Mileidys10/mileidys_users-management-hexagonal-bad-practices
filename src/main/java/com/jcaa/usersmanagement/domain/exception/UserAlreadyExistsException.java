package com.jcaa.usersmanagement.domain.exception;

public final class UserAlreadyExistsException extends DomainException {

    private UserAlreadyExistsException(final String message) {
        super(message);
    }

    private static final String EMAIL_ALREADY_EXISTS_MSG = "A user with email '%s' already exists.";

    public static UserAlreadyExistsException becauseEmailAlreadyExists(final String email) {

        return new UserAlreadyExistsException(String.format(EMAIL_ALREADY_EXISTS_MSG, email));
    }
}