package com.jcaa.usersmanagement.infrastructure.adapter.email;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import com.jcaa.usersmanagement.domain.exception.EmailSenderException;
import com.jcaa.usersmanagement.domain.model.EmailDestinationModel;
import java.lang.reflect.Field;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

/**
 * Tests para JavaMailEmailSenderAdapter.
 *
 * <p>Cubre el envío exitoso de correos mediante SMTP, el manejo de excepciones de mensajería
 * (MessagingException) y la correcta configuración de las credenciales de autenticación.
 */
@DisplayName("JavaMailEmailSenderAdapter")
class JavaMailEmailSenderAdapterTest {

  private static final String HOST = "smtp.example.com";
  private static final int PORT = 587;
  private static final String USERNAME = "user@example.com";
  private static final String PASSWORD = "secret";
  private static final String FROM_ADDRESS = "noreply@example.com";
  private static final String FROM_NAME = "App Notifications";
  private static final String DEST_EMAIL = "john@example.com";
  private static final String DEST_NAME = "John Doe";
  private static final String SUBJECT = "Account created";
  private static final String BODY = "<html>Welcome</html>";

  private JavaMailEmailSenderAdapter adapter;
  private EmailDestinationModel destination;

  @BeforeEach
  void setUp() {
    final SmtpConfig config =
        new SmtpConfig(HOST, PORT, USERNAME, PASSWORD, FROM_ADDRESS, FROM_NAME);
    adapter = new JavaMailEmailSenderAdapter(config);
    destination = new EmailDestinationModel(DEST_EMAIL, DEST_NAME, SUBJECT, BODY);
  }

  @Test
  @DisplayName("Debe invocar el envío de transporte exactamente una vez cuando el SMTP es exitoso")
  void shouldDispatchMessageWhenSmtpSucceeds() {
    // Arrange
    try (final MockedStatic<Transport> mockedTransport = mockStatic(Transport.class)) {

      // Act
      adapter.send(destination);

      // Assert
      mockedTransport.verify(() -> Transport.send(any(Message.class)));
    }
  }

  @Test
  @DisplayName("Debe encapsular MessagingException en EmailSenderException cuando falla el transporte")
  void shouldThrowEmailSenderExceptionWhenTransportFails() {
    // Arrange
    final MessagingException smtpError = new MessagingException("Connection refused");

    try (final MockedStatic<Transport> mockedTransport = mockStatic(Transport.class)) {
      mockedTransport.when(() -> Transport.send(any(Message.class))).thenThrow(smtpError);

      // Act & Assert
      final EmailSenderException exception =
          assertThrows(EmailSenderException.class, () -> adapter.send(destination));

      assertTrue(exception.getMessage().contains(DEST_EMAIL));
    }
  }

  @Test
  @SuppressWarnings("java:S3011") // Reflexión necesaria para acceder a mailSession privado en el test
  @DisplayName("Debe proporcionar las credenciales configuradas cuando se invoca el autenticador")
  void shouldProvideConfiguredCredentialsWhenAuthenticatorIsInvoked() throws Exception {
    // Arrange
    final Field sessionField = JavaMailEmailSenderAdapter.class.getDeclaredField("mailSession");
    sessionField.setAccessible(true);
    final Session mailSession = (Session) sessionField.get(adapter);

    // Act
    final PasswordAuthentication auth =
        mailSession.requestPasswordAuthentication(null, PORT, "smtp", "Login", USERNAME);

    // Assert
    assertAll(
        "Verificación de credenciales SMTP",
        () -> assertEquals(USERNAME, auth.getUserName()),
        () -> assertEquals(PASSWORD, auth.getPassword()));
  }
}
