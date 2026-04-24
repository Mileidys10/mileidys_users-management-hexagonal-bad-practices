package com.jcaa.usersmanagement.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.application.port.out.GetUserByEmailPort;
import com.jcaa.usersmanagement.application.service.dto.command.LoginCommand;
import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.exception.InvalidCredentialsException;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests para LoginService.
 *
 * <p>Cubre la autenticación de usuarios validando credenciales correctas,
 * manejo de correos no registrados, contraseñas incorrectas y estados de cuenta.
 */
@DisplayName("LoginService")
@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

  @Mock private GetUserByEmailPort getUserByEmailPort;

  private LoginService service;

  private static final String EMAIL = "john@example.com";
  private static final String PASSWORD = "SecurePass1";

  @BeforeEach
  void setUp() {
    try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      service = new LoginService(getUserByEmailPort, validatorFactory.getValidator());
    }
  }

  @Test
  @DisplayName("Debe retornar el usuario cuando las credenciales son válidas y la cuenta está activa")
  void shouldReturnUserWhenCredentialsAreValidAndUserIsActive() {
    // Arrange
    final LoginCommand command = new LoginCommand(EMAIL, PASSWORD);
    final UserModel activeUser =
        new UserModel(
            new UserId("u-001"),
            new UserName("John Arrieta"),
            new UserEmail(EMAIL),
            UserPassword.fromPlainText(PASSWORD),
            UserRole.ADMIN,
            UserStatus.ACTIVE);
    when(getUserByEmailPort.getByEmail(any())).thenReturn(Optional.of(activeUser));

    // Act
    final UserModel result = service.execute(command);

    // Assert
    assertNotNull(result);
    assertSame(activeUser, result);
  }

  @Test
  @DisplayName("Debe lanzar InvalidCredentialsException cuando el correo no está registrado")
  void shouldThrowWhenEmailNotFound() {
    // Arrange
    final LoginCommand command = new LoginCommand(EMAIL, PASSWORD);
    when(getUserByEmailPort.getByEmail(any())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(InvalidCredentialsException.class, () -> service.execute(command));
  }

  @Test
  @DisplayName("Debe lanzar InvalidCredentialsException cuando la contraseña es incorrecta")
  void shouldThrowWhenPasswordIsWrong() {
    // Arrange
    final LoginCommand command = new LoginCommand(EMAIL, "WrongPass99");
    final UserModel user =
        new UserModel(
            new UserId("u-001"),
            new UserName("John Arrieta"),
            new UserEmail(EMAIL),
            UserPassword.fromPlainText(PASSWORD),
            UserRole.MEMBER,
            UserStatus.ACTIVE);
    when(getUserByEmailPort.getByEmail(any())).thenReturn(Optional.of(user));

    // Act & Assert
    assertThrows(InvalidCredentialsException.class, () -> service.execute(command));
  }

  @Test
  @DisplayName("Debe lanzar InvalidCredentialsException cuando el usuario no está en estado ACTIVE")
  void shouldThrowWhenUserIsNotActive() {
    // Arrange
    final LoginCommand command = new LoginCommand(EMAIL, PASSWORD);
    final UserModel pendingUser =
        new UserModel(
            new UserId("u-001"),
            new UserName("John Arrieta"),
            new UserEmail(EMAIL),
            UserPassword.fromPlainText(PASSWORD),
            UserRole.MEMBER,
            UserStatus.PENDING);
    when(getUserByEmailPort.getByEmail(any())).thenReturn(Optional.of(pendingUser));

    // Act & Assert
    assertThrows(InvalidCredentialsException.class, () -> service.execute(command));
  }

  @Test
  @DisplayName("Debe lanzar ConstraintViolationException cuando el comando tiene campos inválidos")
  void shouldThrowWhenCommandIsInvalid() {
    // Arrange
    final LoginCommand command = new LoginCommand("no-es-email", "short");

    // Act & Assert
    assertThrows(ConstraintViolationException.class, () -> service.execute(command));
    verifyNoInteractions(getUserByEmailPort);
  }
}
