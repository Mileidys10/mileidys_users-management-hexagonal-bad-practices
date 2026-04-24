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
 * Tests para UserCreatedDomainEvent.
 *
 * <p>Verifica que el evento de dominio capture correctamente los datos del usuario,
 * el nombre del evento y el instante en que ocurrió.
 */
@DisplayName("UserCreatedDomainEvent")
class UserCreatedDomainEventTest {

  private static final String ID = "user-001";
  private static final String NAME = "John Arrieta";
  private static final String EMAIL = "john.arrieta@gmail.com";
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
            UserRole.MEMBER,
            UserStatus.ACTIVE);
  }

  @Test
  @DisplayName("Debe retornar el nombre de evento correcto")
  void shouldHaveEventNameUserCreated() {
    // Arrange
    final UserCreatedDomainEvent event = new UserCreatedDomainEvent(user);

    // Act
    final String result = event.getEventName();

    // Assert
    assertEquals("user.created", result);
  }

  @Test
  @DisplayName("Debe registrar el instante de ocurrencia al momento de la creación")
  void shouldRecordOccurredOnAtCreationTime() {
    // Arrange
    final LocalDateTime before = LocalDateTime.now();
    final UserCreatedDomainEvent event = new UserCreatedDomainEvent(user);
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
    final UserCreatedDomainEvent event = new UserCreatedDomainEvent(user);

    // Act
    final UserModel result = event.getUser();

    // Assert
    assertSame(user, result);
  }

  @Test
  @DisplayName("Debe contener todos los campos del usuario en el payload")
  void shouldReturnPayloadWithAllUserFields() {
    // Arrange
    final UserCreatedDomainEvent event = new UserCreatedDomainEvent(user);

    // Act
    final Map<String, String> payload = event.payload();

    // Assert
    assertAll(
        "payload de UserCreatedDomainEvent",
        () -> assertEquals(5, payload.size()),
        () -> assertEquals(ID, payload.get("id")),
        () -> assertEquals(NAME, payload.get("name")),
        () -> assertEquals(EMAIL, payload.get("email")),
        () -> assertEquals(UserRole.MEMBER.name(), payload.get("role")),
        () -> assertEquals(UserStatus.ACTIVE.name(), payload.get("status")));
  }
}
