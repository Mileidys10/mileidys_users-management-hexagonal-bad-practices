package com.jcaa.usersmanagement.domain.event;

import static org.junit.jupiter.api.Assertions.*;

import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests para UserUpdatedDomainEvent.
 *
 * <p>Verifica que el evento de actualización capture correctamente los datos del usuario,
 * el nombre del evento y el instante en que ocurrió.
 */
@DisplayName("UserUpdatedDomainEvent")
class UserUpdatedDomainEventTest {

  private static final String ID = "user-003";
  private static final String NAME = "Jane Doe";
  private static final String EMAIL = "jane.doe@example.com";
  private static final String HASH = "$2a$12$abcdefghijklmnopqrstuO";

  private UserModel user;

  @BeforeEach
  void setUp() {
    user =
        new UserModel(
            new UserId(ID),
            new UserName(NAME),
            new UserEmail(EMAIL),
            UserPassword.fromHash(HASH),
            UserRole.ADMIN,
            UserStatus.INACTIVE);
  }

  @Test
  @DisplayName("Debe retornar el nombre de evento correcto")
  void shouldHaveEventNameUserUpdated() {
    // Arrange
    final UserUpdatedDomainEvent event = new UserUpdatedDomainEvent(user);

    // Act
    final String result = event.getEventName();

    // Assert
    assertEquals("user.updated", result);
  }

  @Test
  @DisplayName("Debe registrar el instante de ocurrencia al momento de la creación")
  void shouldRecordOccurredOnAtCreationTime() {
    // Arrange
    final LocalDateTime before = LocalDateTime.now();
    final UserUpdatedDomainEvent event = new UserUpdatedDomainEvent(user);
    final LocalDateTime after = LocalDateTime.now();

    // Act
    final LocalDateTime occurredOn = event.getOccurredOn();

    // Assert
    assertNotNull(occurredOn);
    assertFalse(occurredOn.isBefore(before));
    assertFalse(occurredOn.isAfter(after));
  }

  @Test
  @DisplayName("Debe devolver la misma instancia de usuario recibida")
  void shouldReturnSameUserInstance() {
    // Arrange
    final UserUpdatedDomainEvent event = new UserUpdatedDomainEvent(user);

    // Act
    final UserModel result = event.getUser();

    // Assert
    assertSame(user, result);
  }

  @Test
  @DisplayName("Debe contener todos los campos del usuario actualizado en el payload")
  void shouldReturnPayloadWithAllUserFields() {
    // Arrange
    final UserUpdatedDomainEvent event = new UserUpdatedDomainEvent(user);

    // Act
    final Map<String, String> payload = event.payload();

    // Assert
    assertAll(
        "payload de UserUpdatedDomainEvent",
        () -> assertEquals(5, payload.size()),
        () -> assertEquals(ID, payload.get("id")),
        () -> assertEquals(NAME, payload.get("name")),
        () -> assertEquals(EMAIL, payload.get("email")),
        () -> assertEquals(UserRole.ADMIN.name(), payload.get("role")),
        () -> assertEquals(UserStatus.INACTIVE.name(), payload.get("status")));
  }
}
