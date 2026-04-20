package com.jcaa.usersmanagement.domain.exception;

public final class InvalidUserIdException extends DomainException {

  private InvalidUserIdException(final String message) {
    super(message);
  }
private static final String INVALID_ID_MSG = "The user id must not be empty.";

  public static InvalidUserIdException becauseValueIsEmpty() {

    return new InvalidUserIdException(INVALID_ID_MSG);
  }
}
