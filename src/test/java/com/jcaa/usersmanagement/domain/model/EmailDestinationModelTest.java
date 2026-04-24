package com.jcaa.usersmanagement.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests para EmailDestinationModel.
 *
 * <p>
 * Verifica que el modelo de destino de correo se construya correctamente
 * y que las validaciones de campos obligatorios (no nulos ni vacíos) se
 * apliquen.
 */
@DisplayName("EmailDestinationModel")
class EmailDestinationModelTest {

  private static final String EMAIL = "dest@example.com";
  private static final String NAME = "Recipient Name";
  private static final String SUBJECT = "Welcome";
  private static final String BODY = "Hello, welcome to the platform.";

  @Test
  @DisplayName("Debe preservar todos los campos cuando los datos proporcionados son válidos")
  void shouldCreateModelWithAllValidFields() {
    // Arrange & Act
    final EmailDestinationModel model = new EmailDestinationModel(EMAIL, NAME, SUBJECT, BODY);

    // Assert
    assertAll(
        "Verificación de campos del modelo de destino",
        () -> assertEquals(EMAIL, model.destinationEmail()),
        () -> assertEquals(NAME, model.destinationName()),
        () -> assertEquals(SUBJECT, model.subject()),
        () -> assertEquals(BODY, model.body()));
  }

  @Test
  @DisplayName("Debe lanzar NullPointerException cuando el correo de destino es nulo")
  void shouldThrowNpeWhenDestinationEmailIsNull() {
    // Act & Assert
    assertThrows(
        NullPointerException.class, () -> new EmailDestinationModel(null, NAME, SUBJECT, BODY));
  }

  @Test
  @DisplayName("Debe lanzar IllegalArgumentException cuando el nombre de destino está en blanco")
  void shouldThrowIaeWhenDestinationNameIsBlank() {
    // Act & Assert
    assertThrows(
        IllegalArgumentException.class,
        () -> new EmailDestinationModel(EMAIL, "   ", SUBJECT, BODY));
  }

  @Test
  @DisplayName("Debe lanzar NullPointerException cuando el asunto es nulo")
  void shouldThrowNpeWhenSubjectIsNull() {
    // Act & Assert
    assertThrows(
        NullPointerException.class, () -> new EmailDestinationModel(EMAIL, NAME, null, BODY));
  }

  @Test
  @DisplayName("Debe lanzar IllegalArgumentException cuando el cuerpo del mensaje está vacío")
  void shouldThrowIaeWhenBodyIsEmpty() {
    // Act & Assert
    assertThrows(
        IllegalArgumentException.class, () -> new EmailDestinationModel(EMAIL, NAME, SUBJECT, ""));
  }
}
