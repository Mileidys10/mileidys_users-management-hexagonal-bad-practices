package com.jcaa.usersmanagement.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests para UserModel.
 *
 * <p>Verifica la creación de usuarios con estado inicial pendiente y las transiciones
 * de estado a activo e inactivo, asegurando la inmutabilidad de la instancia original.
 */
@DisplayName("UserModel")
class UserModelTest {

  private static final String HASH = "$2a$12$abcdefghijklmnopqrstuO";

  private UserId userId;
  private UserName userName;
  private UserEmail userEmail;
  private UserPassword password;

  @BeforeEach
  void setUp() {
    userId = new UserId("u-001");
    userName = new UserName("Alice Smith");
    userEmail = new UserEmail("alice@example.com");
    password = UserPassword.fromHash(HASH);
  }

  @Test
  @DisplayName("Debe fijar el estado PENDING y preservar los campos al crear un nuevo usuario")
  void shouldCreateUserWithPendingStatusAndPreserveAllFields() {
    // Act
    final UserModel model =
        UserModel.create(userId, userName, userEmail, password, UserRole.MEMBER);

    // Assert
    assertAll(
        "Verificación de creación de usuario",
        () -> assertEquals(UserStatus.PENDING, model.getStatus()),
        () -> assertSame(password, model.getPassword()));
  }

  @Test
  @DisplayName("Debe retornar una nueva instancia con estado ACTIVE al activar el usuario")
  void shouldActivateAndPreserveOtherFields() {
    // Arrange
    final UserModel pending =
        UserModel.create(userId, userName, userEmail, password, UserRole.REVIEWER);

    // Act
    final UserModel activated = pending.activate();

    // Assert
    assertAll(
        "Verificación de activación de usuario",
        () -> assertNotSame(pending, activated),
        () -> assertEquals(UserStatus.ACTIVE, activated.getStatus()),
        () -> assertSame(userId, activated.getId()),
        () -> assertSame(userName, activated.getName()),
        () -> assertSame(userEmail, activated.getEmail()),
        () -> assertEquals(UserRole.REVIEWER, activated.getRole()));
  }

  @Test
  @DisplayName("Debe retornar una nueva instancia con estado INACTIVE al desactivar el usuario")
  void shouldDeactivateAndPreserveOtherFields() {
    // Arrange
    final UserModel active =
        new UserModel(userId, userName, userEmail, password, UserRole.ADMIN, UserStatus.ACTIVE);

    // Act
    final UserModel deactivated = active.deactivate();

    // Assert
    assertAll(
        "Verificación de desactivación de usuario",
        () -> assertNotSame(active, deactivated),
        () -> assertEquals(UserStatus.INACTIVE, deactivated.getStatus()),
        () -> assertSame(userId, deactivated.getId()),
        () -> assertEquals(UserRole.ADMIN, deactivated.getRole()));
  }
}
