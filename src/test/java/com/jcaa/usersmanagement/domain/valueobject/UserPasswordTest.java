package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import com.jcaa.usersmanagement.domain.exception.InvalidUserPasswordException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests para el objeto de valor UserPassword.
 *
 * <p>Verifica el cifrado de contraseñas en texto plano, la validación de longitud,
 * la creación desde hashes existentes y la correcta implementación de igualdad y hashcode.
 */
@DisplayName("UserPassword")
class UserPasswordTest {

  @ParameterizedTest
  @ValueSource(strings = {"password123", "   password123   "})
  @DisplayName("Debe normalizar y cifrar la contraseña cuando se proporciona en texto plano")
  void shouldNormalizeAndHashPassword(final String input) {
    // Act
    final UserPassword result = UserPassword.fromPlainText(input);

    // Assert
    assertNotNull(result.value());
    assertNotEquals(input.trim(), result.value());
  }

  @ParameterizedTest
  @ValueSource(strings = {"clave", "    clave     "})
  @DisplayName("Debe lanzar excepción cuando la contraseña normalizada es demasiado corta")
  void shouldFailWhenPasswordIsTooShort(final String password) {
    // Act & Assert
    assertThrows(InvalidUserPasswordException.class, () -> UserPassword.fromPlainText(password));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "  ", "\r", "\t", "\n", "\f", "\b", "\0"})
  @DisplayName("Debe lanzar excepción cuando la contraseña está vacía o compuesta solo por espacios")
  void shouldThrowWhenPasswordIsEmptyOrBlank(final String password) {
    // Act & Assert
    assertThrows(InvalidUserPasswordException.class, () -> UserPassword.fromPlainText(password));
  }

  @Test
  @DisplayName("Debe lanzar NullPointerException cuando la contraseña proporcionada es nula")
  void shouldThrowWhenPasswordIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class, () -> UserPassword.fromPlainText(null));
  }

  @Test
  @DisplayName("Debe verificar correctamente la contraseña cifrada contra su original en texto plano")
  void shouldVerifyPlainPassword() {
    // Arrange
    final String plainPassword = "mySecurePassword";

    // Act
    final UserPassword userPassword = UserPassword.fromPlainText(plainPassword);

    // Assert
    assertTrue(userPassword.verifyPlain(plainPassword));
  }

  @Test
  @DisplayName("Debe permitir la reconstrucción del objeto desde un hash persistido y validar contra el original")
  void shouldCreateUserPasswordFromExistingHash() {
    // Arrange
    final String rawPassword = "Abcde1234567";
    final UserPassword originalUserPassword = UserPassword.fromPlainText(rawPassword);
    final String generatedHash = originalUserPassword.value();

    // Act
    final UserPassword fromHashUserPassword = UserPassword.fromHash(generatedHash);

    // Assert
    assertEquals(originalUserPassword, fromHashUserPassword);
    assertTrue(fromHashUserPassword.verifyPlain(rawPassword));
  }

  @Test
  @DisplayName("Debe retornar falso al comparar con un objeto que no es una instancia de contraseña")
  void shouldReturnFalseWhenOtherIsNotInstanceOfUserPassword() {
    // Arrange
    final UserPassword password = UserPassword.fromPlainText("MiPassword123");
    final Object nonUserPassword = mock(Object.class);

    // Act
    final boolean areEqual = password.equals(nonUserPassword);

    // Assert
    assertFalse(areEqual);
  }

  @Test
  @DisplayName("Debe retornar falso al comparar contraseñas con hashes distintos")
  void shouldReturnFalseWhenDifferentHash() {
    // Arrange
    final UserPassword a = UserPassword.fromPlainText("MiPassword123");
    final UserPassword b = UserPassword.fromPlainText("OtroPassword456");

    // Assert
    assertNotEquals(a, b);
  }

  @Test
  @DisplayName("Debe producir un hashCode consistente para la misma instancia")
  void shouldReturnConsistentHashCode() {
    // Arrange
    final UserPassword password = UserPassword.fromPlainText("MiPassword123");

    // Act
    final int firstHashCode = password.hashCode();
    final int secondHashCode = password.hashCode();

    // Assert
    assertEquals(firstHashCode, secondHashCode);
  }

  @Test
  @DisplayName("Debe producir el mismo hashCode para objetos que son considerados iguales")
  void shouldHaveSameHashCodeWhenEqual() {
    // Arrange
    final UserPassword a = UserPassword.fromPlainText("MiPassword123");
    final UserPassword b = UserPassword.fromHash(a.value());

    // Assert
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
  }
}
