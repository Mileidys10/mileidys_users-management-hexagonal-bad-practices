package com.jcaa.usersmanagement.domain.valueobject;

import static org.junit.jupiter.api.Assertions.*;

import com.jcaa.usersmanagement.domain.exception.InvalidUserNameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests para el objeto de valor UserName.
 *
 * <p>Verifica la normalización de nombres mediante el recorte de espacios,
 * la validación de longitud mínima y el manejo de valores nulos o vacíos.
 */
@DisplayName("UserName")
class UserNameTest {

  @ParameterizedTest
  @ValueSource(strings = {"John Arrieta", "   John Arrieta   ", "John Arrieta \t"})
  @DisplayName("Debe crear el nombre de usuario eliminando los espacios en blanco circundantes")
  void shouldValidateUserNameMinimumLength(final String userName) {
    // Arrange
    final String expectedUserName = "John Arrieta";

    // Act
    final UserName userNameVo = new UserName(userName);

    // Assert
    assertEquals(expectedUserName, userNameVo.toString());
  }

  @Test
  @DisplayName("Debe lanzar NullPointerException cuando el nombre de usuario es nulo")
  void shouldValidateUserNameIsNotNull() {
    // Act & Assert
    assertThrows(NullPointerException.class, () -> new UserName(null));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"", "  ", "\t", "\n", "\r", "\f", "\b", "Jo", "Ty  ", "", "   Cy ", "Ed\t"})
  @DisplayName("Debe lanzar InvalidUserNameException cuando el nombre es corto, vacío o solo espacios")
  void shouldValidateUserNameIsNotEmptyAndMinimumLength(final String userName) {
    // Act & Assert
    assertThrows(InvalidUserNameException.class, () -> new UserName(userName));
  }
}
