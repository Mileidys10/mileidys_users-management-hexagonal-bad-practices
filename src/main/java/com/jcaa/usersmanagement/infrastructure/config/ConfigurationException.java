package com.jcaa.usersmanagement.infrastructure.config;

public final class ConfigurationException extends RuntimeException {

  private ConfigurationException(final String message, final Throwable cause) {
    super(message, cause);
  }
    private static final String FAILED_APPLICATION_MSG ="Failed to load the application configuration.";

  public static ConfigurationException becauseLoadFailed(final Throwable cause) {

    return new ConfigurationException(FAILED_APPLICATION_MSG, cause);
  }
}
