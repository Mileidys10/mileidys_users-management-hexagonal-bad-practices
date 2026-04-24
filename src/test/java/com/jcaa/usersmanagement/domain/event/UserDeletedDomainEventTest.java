package com.jcaa.usersmanagement.domain.event;

import static org.junit.jupiter.api.Assertions.*;

import com.jcaa.usersmanagement.domain.valueobject.UserId;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests para UserDeletedDomainEvent.
 *
 * <p>Verifica que el evento de eliminación capture correctamente el identificador del usuario,
 * el nombre del evento y el instante en que ocurrió.
 */
@DisplayName("UserDeletedDomainEvent")
class UserDeletedDomainEventTest {

  private static final String ID = "user-002";

  @Test
  @DisplayName("Debe retornar el nombre de evento correcto")
  void shouldHaveEventNameUserDeleted() {
    // Arrange
    final UserDeletedDomainEvent event = new UserDeletedDomainEvent(new UserId(ID));

    // Act
    final String result = event.getEventName();

    // Assert
    assertEquals("user.deleted", result);
  }

  @Test
  @DisplayName("Debe registrar el instante de ocurrencia al momento de la creación")
  void shouldRecordOccurredOnAtCreationTime() {
    // Arrange
    final LocalDateTime before = LocalDateTime.now();
    final UserDeletedDomainEvent event = new UserDeletedDomainEvent(new UserId(ID));
    final LocalDateTime after = LocalDateTime.now();

    // Act
    final LocalDateTime occurredOn = event.getOccurredOn();

    // Assert
    assertNotNull(occurredOn);
    assertFalse(occurredOn.isBefore(before));
    assertFalse(occurredOn.isAfter(after));
  }

  @Test
  @DisplayName("Debe devolver la misma instancia de identificador de usuario recibida")
  void shouldReturnSameUserIdInstance() {
    // Arrange
    final UserId userId = new UserId(ID);
    final UserDeletedDomainEvent event = new UserDeletedDomainEvent(userId);

    // Act
    final UserId result = event.getUserId();

    // Assert
    assertSame(userId, result);
  }

  @Test
  @DisplayName("Debe contener únicamente el identificador del usuario en el payload")
  void shouldReturnPayloadWithOnlyUserId() {
    // Arrange
    final UserDeletedDomainEvent event = new UserDeletedDomainEvent(new UserId(ID));

    // Act
    final Map<String, String> payload = event.payload();

    // Assert
    assertEquals(1, payload.size());
    assertEquals(ID, payload.get("id"));
  }
}
