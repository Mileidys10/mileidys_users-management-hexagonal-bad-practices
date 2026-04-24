package com.jcaa.usersmanagement.domain.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests para InvalidCredentialsException.
 *
 * <p>Verifica que las excepciones de credenciales inválidas y usuario inactivo
 * produzcan mensajes descriptivos y diferenciados.
 */
@DisplayName("InvalidCredentialsException")
class InvalidCredentialsExceptionTest {

  @Test
  @DisplayName("Debe producir mensajes distintos y válidos para cada escenario de fallo en autenticación")
  void shouldProduceDistinctNonBlankMessagesForEachAuthFailureScenario() {
    // Arrange (No setup required for this stateless test)

    // Act
    final String invalidCredsMsg =
        InvalidCredentialsException.becauseCredentialsAreInvalid().getMessage();
    final String inactiveUserMsg =
        InvalidCredentialsException.becauseUserIsNotActive().getMessage();

    // Assert
    assertNotNull(invalidCredsMsg);
    assertNotNull(inactiveUserMsg);
    assertFalse(invalidCredsMsg.isBlank());
    assertFalse(inactiveUserMsg.isBlank());
    assertNotEquals(invalidCredsMsg, inactiveUserMsg);
  }
}
