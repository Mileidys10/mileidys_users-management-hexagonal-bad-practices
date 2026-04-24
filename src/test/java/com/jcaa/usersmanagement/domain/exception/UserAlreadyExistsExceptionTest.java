package com.jcaa.usersmanagement.domain.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests para UserAlreadyExistsException.
 *
 * <p>Verifica que la excepción capture e incluya el correo electrónico duplicado
 * en su mensaje de error para facilitar el diagnóstico.
 */
@DisplayName("UserAlreadyExistsException")
class UserAlreadyExistsExceptionTest {

  @Test
  @DisplayName("Debe incluir el correo electrónico duplicado en el mensaje de error")
  void shouldIncludeEmailInMessage() {
    // Arrange
    final String email = "existing@example.com";

    // Act
    final String message = UserAlreadyExistsException.becauseEmailAlreadyExists(email).getMessage();

    // Assert
    assertTrue(message.contains(email));
  }
}
