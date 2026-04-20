package com.jcaa.usersmanagement.domain.exception;

public final class InvalidUserNameException extends DomainException {

  private InvalidUserNameException(final String message) {
    super(message);
  }


private static final String EMPTY_NAME_VALUE_MSG = "The user name must not be empty.";
private static final String SHORT_NAME_VALUE_MSG = "The user name must have at least %d characters.";

  public static InvalidUserNameException becauseValueIsEmpty() {

    return new InvalidUserNameException(EMPTY_NAME_VALUE_MSG);
  }

  public static InvalidUserNameException becauseLengthIsTooShort(final int minimumLength) {
    return new InvalidUserNameException(
        String.format(SHORT_NAME_VALUE_MSG, minimumLength));
  }
}
