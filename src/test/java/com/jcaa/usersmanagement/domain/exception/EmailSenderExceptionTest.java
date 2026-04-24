package com.jcaa.usersmanagement.domain.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests para EmailSenderException.
 *
 * <p>Verifica la creación de excepciones por errores SMTP y fallos genéricos de envío,
 * asegurando que los mensajes y causas se capturen correctamente.
 */
@DisplayName("EmailSenderException")
class EmailSenderExceptionTest {

  @Test
  @DisplayName("Debe formatear el mensaje incluyendo el email y el error SMTP cuando falla el protocolo")
  void shouldFormatMessageWithEmailAndSmtpError() {
    // Arrange
    final String destinationEmail = "user@example.com";
    final String smtpError = "Connection refused";

    // Act
    final String message = EmailSenderException.becauseSmtpFailed(destinationEmail, smtpError).getMessage();

    // Assert
    assertTrue(message.contains(destinationEmail));
    assertTrue(message.contains(smtpError));
  }

  @Test
  @DisplayName("Debe encapsular la causa original y producir un mensaje válido cuando falla el envío")
  void shouldWrapCauseAndProduceNonBlankMessage() {
    // Arrange
    final Throwable cause = new RuntimeException("IO error");

    // Act
    final EmailSenderException exception = EmailSenderException.becauseSendFailed(cause);

    // Assert
    assertSame(cause, exception.getCause());
    assertFalse(exception.getMessage().isBlank());
  }
}
