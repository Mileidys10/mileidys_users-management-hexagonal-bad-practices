package com.jcaa.usersmanagement.infrastructure.config;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests para AppProperties.
 *
 * <p>Verifica la carga de propiedades desde el classpath, la gestión de flujos de entrada
 * (InputStream), el manejo de errores de configuración y la recuperación de valores tipo String y
 * enteros.
 */
@DisplayName("AppProperties")
class AppPropertiesTest {

  private static final String KEY_STRING = "db.host";
  private static final String KEY_INT = "db.port";
  private static final String KEY_MISSING = "nonexistent.key";

  private static final String EXPECTED_STRING = "localhost";
  private static final int EXPECTED_INT = 3306;

  private AppProperties appProperties;

  @BeforeEach
  void setUp() {
    appProperties = new AppProperties();
  }

  @Test
  @DisplayName(
      "Debe cargar correctamente el archivo de propiedades del sistema al instanciar la clase")
  void shouldLoadPropertiesFileWithoutThrowing() {
    // Assert
    assertNotNull(appProperties);
  }

  @Test
  @DisplayName("Debe cargar las propiedades correctamente desde un flujo de entrada válido")
  void shouldLoadPropertiesFromValidStream() throws IOException {
    // Arrange
    final String content = "custom.key=custom.value\n";

    // Act
    try (final InputStream stream =
        new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))) {
      final AppProperties loaded = new AppProperties(stream);

      // Assert
      assertEquals("custom.value", loaded.get("custom.key"));
    }
  }

  @Test
  @DisplayName("Debe lanzar NullPointerException cuando el flujo de entrada proporcionado es nulo")
  void shouldThrowNullPointerExceptionWhenStreamIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class, () -> new AppProperties(null));
  }

  @Test
  @DisplayName(
      "Debe lanzar ConfigurationException cuando ocurre un error de lectura en el flujo de entrada")
  void shouldThrowConfigurationExceptionOnIOException() throws IOException {
    // Arrange
    try (final InputStream failingStream =
        new InputStream() {
          @Override
          public int read() throws IOException {
            throw new IOException("Simulated read failure");
          }

          @Override
          public int read(final byte[] buffer, final int offset, final int length)
              throws IOException {
            throw new IOException("Simulated read failure");
          }
        }) {

      // Act & Assert
      assertThrows(ConfigurationException.class, () -> new AppProperties(failingStream));
    }
  }

  @Test
  @DisplayName("Debe retornar el valor correcto cuando la clave existe en las propiedades")
  void shouldReturnCorrectValueForExistingKey() {
    // Act
    final String result = appProperties.get(KEY_STRING);

    // Assert
    assertEquals(EXPECTED_STRING, result);
  }

  @Test
  @DisplayName("Debe lanzar NullPointerException detallando la clave cuando esta no existe")
  void shouldThrowNullPointerExceptionForMissingKey() {
    // Act & Assert
    final NullPointerException exception =
        assertThrows(NullPointerException.class, () -> appProperties.get(KEY_MISSING));

    assertTrue(exception.getMessage().contains(KEY_MISSING));
  }

  @Test
  @DisplayName("Debe retornar el valor entero parseado correctamente para una clave numérica")
  void shouldReturnParsedIntForNumericKey() {
    // Act
    final int result = appProperties.getInt(KEY_INT);

    // Assert
    assertEquals(EXPECTED_INT, result);
  }

  @Test
  @DisplayName(
      "Debe lanzar NumberFormatException cuando el valor asociado a la clave no es un entero válido")
  void shouldThrowNumberFormatExceptionForNonIntegerValue() {
    // Act & Assert
    assertThrows(NumberFormatException.class, () -> appProperties.getInt(KEY_STRING));
  }
}
