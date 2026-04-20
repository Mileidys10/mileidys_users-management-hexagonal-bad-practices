package com.jcaa.usersmanagement.domain.exception;

public final class UserNotFoundException extends DomainException {

  private UserNotFoundException(final String message) {
    super(message);
  }
private static final String NOT_FOUND_ID_MSG = "The user with id '%s' was not found.";

  public static UserNotFoundException becauseIdWasNotFound(final String userId) {
    return new UserNotFoundException(String.format(NOT_FOUND_ID_MSG, userId));
  }
}
