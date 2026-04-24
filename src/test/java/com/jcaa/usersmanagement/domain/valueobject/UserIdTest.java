package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.*;

import com.jcaa.usersmanagement.domain.exception.InvalidUserIdException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests para el objeto de valor UserId.
 *
 * <p>Verifica la normalización de identificadores mediante el recorte de espacios
 * y la validación de valores nulos o vacíos.
 */
@DisplayName("UserId")
class UserIdTest {

  @ParameterizedTest
  @ValueSource(strings = {" user123 ", "  user123  ", "user123\t"})
  @DisplayName("Debe crear el identificador eliminando los espacios en blanco circundantes")
  void shouldCreateUserIdWithTrimmedValue(String input) {
    // Arrange
    final String expectedUserId = "user123";

    // Act
    final UserId userId = new UserId(input);

    // Assert
    assertEquals(expectedUserId, userId.toString());
  }

  @Test
  @DisplayName("Debe lanzar NullPointerException cuando el identificador es nulo")
  void shouldThrowNullPointerExceptionWhenUserIdIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class, () -> new UserId(null));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   ", "\t", "\n", "\r", "\f", "\b"})
  @DisplayName("Debe lanzar InvalidUserIdException cuando el identificador está vacío o compuesto solo por espacios")
  void shouldThrowIllegalArgumentExceptionWhenUserIdIsEmpty(String input) {
    // Act & Assert
    assertThrows(InvalidUserIdException.class, () -> new UserId(input));
  }
}
