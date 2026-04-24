package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.*;

import com.jcaa.usersmanagement.domain.exception.InvalidUserEmailException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests para el objeto de valor UserEmail.
 *
 * <p>Verifica la normalización, validación de formato y consistencia de representación
 * de los correos electrónicos de usuario.
 */
@DisplayName("UserEmail")
class UserEmailTest {

  @Test
  @DisplayName("Debe normalizar el correo eliminando espacios y convirtiendo a minúsculas")
  void shouldNormalizeEmail() {
    // Arrange
    final String expectedEmail = "john.arrieta@gmail.com";
    final String inputEmail = "  john.arrieta@gmail.com  ";

    // Act
    final UserEmail userEmail = new UserEmail(inputEmail);

    // Assert
    assertEquals(expectedEmail, userEmail.value());
  }

  @Test
  @DisplayName("Debe lanzar excepción cuando el correo está compuesto solo por espacios")
  void shouldValidateEmailIsNotEmpty() {
    // Arrange
    final String email = "   ";

    // Act & Assert
    assertThrows(InvalidUserEmailException.class, () -> new UserEmail(email));
  }

  @Test
  @DisplayName("Debe lanzar excepción cuando el formato del correo es inválido")
  void shouldValidateEmailFormat() {
    // Arrange
    final String email = "johnarroeta-arroba-gmail.com";

    // Act & Assert
    assertThrows(InvalidUserEmailException.class, () -> new UserEmail(email));
  }

  @Test
  @DisplayName("Debe retornar el valor del correo al solicitar su representación en String")
  void shouldValidateEmailToString() {
    // Arrange
    final String email = "john.arrieta@gmail.com";

    // Act
    final UserEmail userEmail = new UserEmail(email);

    // Assert
    assertEquals(email, userEmail.toString());
  }

  @Test
  @DisplayName("Debe lanzar NullPointerException cuando el correo proporcionado es nulo")
  void shouldValidateEmailIsNotNull() {
    // Act & Assert
    assertThrows(NullPointerException.class, () -> new UserEmail(null));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "john.arrieta@gmail.com",
        "john-arrieta_arreita@gmail.com.co",
        "john1234567arreita@gmail.com"
      })
  @DisplayName("Debe permitir la creación de correos con formatos válidos diversos")
  void shouldValidateEmailFormatWithParameters(String email) {
    // Act & Assert
    assertDoesNotThrow(() -> new UserEmail(email));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "",
        "johnarroetaarroba-gmail.com",
        "john.arrieta@gmail",
        "john.arrieta@.com",
        "john arrieta@com"
      })
  @DisplayName("Debe prohibir la creación de correos con formatos inválidos")
  void shouldValidateEmailFormatWithInvalidParameters(String email) {
    // Act & Assert
    assertThrows(InvalidUserEmailException.class, () -> new UserEmail(email));
  }
}
