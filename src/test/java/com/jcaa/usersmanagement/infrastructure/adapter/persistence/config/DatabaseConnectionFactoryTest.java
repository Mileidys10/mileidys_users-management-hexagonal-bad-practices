package com.jcaa.usersmanagement.infrastructure.adapter.persistence.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.infrastructure.adapter.persistence.exception.PersistenceException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests para DatabaseConnectionFactory.
 *
 * <p>Verifica la creación de conexiones JDBC mediante DriverManager y el correcto
 * mapeo de excepciones SQLException a excepciones de infraestructura (PersistenceException).
 */
@DisplayName("DatabaseConnectionFactory")
@ExtendWith(MockitoExtension.class)
class DatabaseConnectionFactoryTest {

  private static final String HOST = "localhost";
  private static final int PORT = 3306;
  private static final String DB_NAME = "test_db";
  private static final String USERNAME = "test_user";
  private static final String PASSWORD = "test_pass";

  @Mock private Connection mockConnection;

  private DatabaseConfig config;

  @BeforeEach
  void setUp() {
    config = new DatabaseConfig(HOST, PORT, DB_NAME, USERNAME, PASSWORD);
  }

  @Test
  @DisplayName("Debe retornar la conexión proporcionada por DriverManager cuando los datos son válidos")
  void shouldReturnConnectionWhenDriverManagerSucceeds() {
    // Arrange
    try (final MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
      mockedDriverManager
          .when(() -> DriverManager.getConnection(any(), any(), any()))
          .thenReturn(mockConnection);

      // Act
      final Connection result = DatabaseConnectionFactory.createConnection(config);

      // Assert
      assertSame(mockConnection, result);
    }
  }

  @Test
  @DisplayName("Debe lanzar PersistenceException cuando DriverManager falla al establecer la conexión")
  void shouldThrowPersistenceExceptionWhenDriverManagerFails() {
    // Arrange
    final SQLException cause = new SQLException("Connection refused");

    try (final MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
      mockedDriverManager
          .when(() -> DriverManager.getConnection(any(), any(), any()))
          .thenThrow(cause);

      // Act & Assert
      assertThrows(PersistenceException.class, () -> DatabaseConnectionFactory.createConnection(config));
    }
  }
}
