package com.jcaa.usersmanagement.domain.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests para UserNotFoundException.
 *
 * <p>Verifica que la excepción capture e incluya el identificador del usuario
 * no encontrado en su mensaje de error para facilitar el diagnóstico.
 */
@DisplayName("UserNotFoundException")
class UserNotFoundExceptionTest {

  @Test
  @DisplayName("Debe incluir el identificador del usuario en el mensaje de error")
  void shouldIncludeUserIdInMessage() {
    // Arrange
    final String userId = "user-404";

    // Act
    final String message = UserNotFoundException.becauseIdWasNotFound(userId).getMessage();

    // Assert
    assertTrue(message.contains(userId));
  }
}
