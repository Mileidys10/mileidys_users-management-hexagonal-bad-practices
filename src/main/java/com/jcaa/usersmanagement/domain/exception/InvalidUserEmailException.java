package com.jcaa.usersmanagement.domain.exception;

public final class InvalidUserEmailException extends DomainException {


    private InvalidUserEmailException(final String message) {

        super(message);
    }

    private static final String EMPTY_VALUE_MSG = "The user email must not be empty.";
    private static final String INVALID_EMAIL_MSG = "The user email format is invalid: '%s'.";
    public static InvalidUserEmailException becauseValueIsEmpty() {

        return new InvalidUserEmailException(EMPTY_VALUE_MSG);
    }


  public static InvalidUserEmailException becauseFormatIsInvalid(final String email) {
    return new InvalidUserEmailException(String.format(INVALID_EMAIL_MSG, email));
  }
}
