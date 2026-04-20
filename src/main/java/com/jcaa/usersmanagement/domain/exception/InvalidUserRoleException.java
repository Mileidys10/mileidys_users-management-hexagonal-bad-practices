package com.jcaa.usersmanagement.domain.exception;

public final class InvalidUserRoleException extends DomainException {

    private InvalidUserRoleException(final String message) {
        super(message);
    }

    private static final String INVALID_ROLE_MSG = "The user role '%s' is not valid.";

    public static InvalidUserRoleException becauseValueIsInvalid(final String role) {

        return new InvalidUserRoleException(String.format(INVALID_ROLE_MSG, role));
    }
}
