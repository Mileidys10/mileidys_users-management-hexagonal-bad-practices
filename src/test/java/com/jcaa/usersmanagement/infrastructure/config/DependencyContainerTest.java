package com.jcaa.usersmanagement.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import com.jcaa.usersmanagement.infrastructure.adapter.persistence.exception.PersistenceException;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.controller.UserController;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests para DependencyContainer.
 *
 * <p>Verifica el correcto ensamblaje de todas las dependencias del sistema, garantizando que el
 * grafo de objetos sea inmutable (singleton) y que los fallos de conexión a la base de datos
 * durante la construcción se propaguen correctamente como PersistenceException.
 */
@DisplayName("DependencyContainer")
@ExtendWith(MockitoExtension.class)
class DependencyContainerTest {

  @Mock private Connection mockConnection;

  @Test
  @DisplayName("Debe ensamblar todas las dependencias y exponer un controlador de usuarios no nulo")
  void shouldWireAllDependenciesAndExposeNonNullUserController() {
    // Arrange
    try (final MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
      mockedDriverManager
          .when(
              () ->
                  DriverManager.getConnection(
                      any(String.class), any(String.class), any(String.class)))
          .thenReturn(mockConnection);

      // Act
      final DependencyContainer container = new DependencyContainer();

      // Assert
      assertNotNull(container.userController());
    }
  }

  @Test
  @DisplayName("Debe retornar la misma instancia del controlador de usuarios en cada llamada")
  void shouldReturnTheSameUserControllerInstanceOnEveryCall() {
    // Arrange
    try (final MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
      mockedDriverManager
          .when(
              () ->
                  DriverManager.getConnection(
                      any(String.class), any(String.class), any(String.class)))
          .thenReturn(mockConnection);
      final DependencyContainer container = new DependencyContainer();

      // Act
      final UserController first = container.userController();
      final UserController second = container.userController();

      // Assert
      assertSame(first, second);
    }
  }

  @Test
  @DisplayName(
      "Debe propagar PersistenceException cuando falla la conexión a la base de datos en la construcción")
  void shouldPropagatePersistenceExceptionWhenDatabaseConnectionFails() {
    // Arrange
    final SQLException cause = new SQLException("Connection refused");

    try (final MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
      mockedDriverManager
          .when(
              () ->
                  DriverManager.getConnection(
                      any(String.class), any(String.class), any(String.class)))
          .thenThrow(cause);

      // Act & Assert
      assertThrows(PersistenceException.class, DependencyContainer::new);
    }
  }
}
