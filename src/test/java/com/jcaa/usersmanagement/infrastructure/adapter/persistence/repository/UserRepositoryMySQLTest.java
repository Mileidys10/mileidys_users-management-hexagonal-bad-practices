package com.jcaa.usersmanagement.infrastructure.adapter.persistence.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.exception.UserNotFoundException;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.exception.PersistenceException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests para UserRepositoryMySQL.
 *
 * <p>Verifica las operaciones CRUD en la base de datos MySQL, incluyendo el manejo de
 * inserciones, actualizaciones, consultas por ID/email, eliminaciones y la gestión
 * de excepciones SQL transformadas a PersistenceException.
 */
@DisplayName("UserRepositoryMySQL")
@ExtendWith(MockitoExtension.class)
class UserRepositoryMySQLTest {

  private static final String ID = "u-001";
  private static final String NAME = "John Doe";
  private static final String EMAIL = "john@example.com";
  private static final String HASH = "$2a$12$abcdefghijklmnopqrstuO";
  private static final String ROLE = "ADMIN";
  private static final String STATUS = "ACTIVE";
  private static final String CREATED_AT = "2024-01-01";
  private static final String UPDATED_AT = "2024-01-02";

  @Mock private Connection connection;
  @Mock private PreparedStatement statement;
  @Mock private ResultSet resultSet;

  private UserRepositoryMySQL repository;
  private UserModel userModel;
  private UserId userId;
  private UserEmail userEmail;

  @BeforeEach
  void setUp() {
    repository = new UserRepositoryMySQL(connection);
    userId = new UserId(ID);
    userEmail = new UserEmail(EMAIL);
    userModel =
        new UserModel(
            userId,
            new UserName(NAME),
            userEmail,
            UserPassword.fromHash(HASH),
            UserRole.ADMIN,
            UserStatus.ACTIVE);
  }

  private void configureStatementAndResultSet() throws SQLException {
    when(connection.prepareStatement(anyString())).thenReturn(statement);
    when(statement.executeQuery()).thenReturn(resultSet);
  }

  private void configureResultSetRow() throws SQLException {
    when(resultSet.getString("id")).thenReturn(ID);
    when(resultSet.getString("name")).thenReturn(NAME);
    when(resultSet.getString("email")).thenReturn(EMAIL);
    when(resultSet.getString("password")).thenReturn(HASH);
    when(resultSet.getString("role")).thenReturn(ROLE);
    when(resultSet.getString("status")).thenReturn(STATUS);
    when(resultSet.getString("created_at")).thenReturn(CREATED_AT);
    when(resultSet.getString("updated_at")).thenReturn(UPDATED_AT);
  }

  @Test
  @DisplayName("Debe ejecutar el INSERT y retornar el usuario persistido consultado por ID")
  void shouldSaveUserAndReturnById() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(true);
    configureResultSetRow();

    // Act
    final UserModel result = repository.save(userModel);

    // Assert
    assertAll(
        "Verificación de guardado de usuario",
        () -> assertEquals(ID, result.getId().value()),
        () -> assertEquals(NAME, result.getName().value()),
        () -> assertEquals(EMAIL, result.getEmail().value()));
  }

  @Test
  @DisplayName("Debe lanzar PersistenceException cuando el INSERT falla por error de SQL")
  void shouldThrowPersistenceExceptionWhenInsertFails() throws SQLException {
    // Arrange
    when(connection.prepareStatement(anyString())).thenReturn(statement);
    when(statement.executeUpdate()).thenThrow(new SQLException("Insert failed"));

    // Act & Assert
    assertThrows(PersistenceException.class, () -> repository.save(userModel));
  }

  @Test
  @DisplayName("Debe lanzar UserNotFoundException cuando no se encuentra el usuario guardado tras el INSERT")
  void shouldThrowUserNotFoundExceptionWhenUserNotFoundAfterSave() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(false);

    // Act & Assert
    assertThrows(UserNotFoundException.class, () -> repository.save(userModel));
  }

  @Test
  @DisplayName("Debe ejecutar el UPDATE y retornar el usuario actualizado consultado por ID")
  void shouldUpdateUserAndReturnById() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(true);
    configureResultSetRow();

    // Act
    final UserModel result = repository.update(userModel);

    // Assert
    assertEquals(ID, result.getId().value());
  }

  @Test
  @DisplayName("Debe lanzar PersistenceException cuando el UPDATE falla por error de SQL")
  void shouldThrowPersistenceExceptionWhenUpdateFails() throws SQLException {
    // Arrange
    when(connection.prepareStatement(anyString())).thenReturn(statement);
    when(statement.executeUpdate()).thenThrow(new SQLException("Update failed"));

    // Act & Assert
    assertThrows(PersistenceException.class, () -> repository.update(userModel));
  }

  @Test
  @DisplayName("Debe retornar un Optional con el usuario cuando existe una fila que coincide con el ID")
  void shouldReturnUserWhenFound() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(true);
    configureResultSetRow();

    // Act
    final Optional<UserModel> result = repository.getById(userId);

    // Assert
    assertAll(
        "Verificación de búsqueda por ID",
        () -> assertTrue(result.isPresent()),
        () -> assertEquals(ID, result.get().getId().value()));
  }

  @Test
  @DisplayName("Debe retornar un Optional vacío cuando no existe una fila con el ID proporcionado")
  void shouldReturnEmptyWhenNotFound() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(false);

    // Act
    final Optional<UserModel> result = repository.getById(userId);

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Debe lanzar PersistenceException cuando la preparación de la consulta falla")
  void shouldThrowPersistenceExceptionOnGetByIdFailure() throws SQLException {
    // Arrange
    when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Query failed"));

    // Act & Assert
    assertThrows(PersistenceException.class, () -> repository.getById(userId));
  }

  @Test
  @DisplayName("Debe lanzar PersistenceException cuando la ejecución de la consulta de búsqueda falla")
  void shouldThrowPersistenceExceptionWhenGetByIdExecuteQueryFails() throws SQLException {
    // Arrange
    when(connection.prepareStatement(anyString())).thenReturn(statement);
    when(statement.executeQuery()).thenThrow(new SQLException("Execute query failed"));

    // Act & Assert
    assertThrows(PersistenceException.class, () -> repository.getById(userId));
  }

  @Test
  @DisplayName("Debe lanzar PersistenceException cuando el cierre del PreparedStatement falla")
  void shouldThrowPersistenceExceptionWhenGetByIdStatementCloseFails() throws SQLException {
    // Arrange
    when(connection.prepareStatement(anyString())).thenReturn(statement);
    when(statement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(false);
    doThrow(new SQLException("Close failed")).when(statement).close();

    // Act & Assert
    assertThrows(PersistenceException.class, () -> repository.getById(userId));
  }

  @Test
  @DisplayName("Debe retornar un Optional con el usuario cuando existe una fila que coincide con el email")
  void shouldReturnUserByEmailWhenFound() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(true);
    configureResultSetRow();

    // Act
    final Optional<UserModel> result = repository.getByEmail(userEmail);

    // Assert
    assertAll(
        "Verificación de búsqueda por email",
        () -> assertTrue(result.isPresent()),
        () -> assertEquals(EMAIL, result.get().getEmail().value()));
  }

  @Test
  @DisplayName("Debe retornar un Optional vacío cuando no existe una fila con el email proporcionado")
  void shouldReturnEmptyWhenEmailNotFound() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(false);

    // Act
    final Optional<UserModel> result = repository.getByEmail(userEmail);

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Debe lanzar PersistenceException cuando falla la búsqueda por email por error de SQL")
  void shouldThrowPersistenceExceptionOnGetByEmailFailure() throws SQLException {
    // Arrange
    when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Query failed"));

    // Act & Assert
    assertThrows(PersistenceException.class, () -> repository.getByEmail(userEmail));
  }

  @Test
  @DisplayName("Debe lanzar PersistenceException cuando falla la ejecución de la búsqueda por email")
  void shouldThrowPersistenceExceptionWhenGetByEmailExecuteQueryFails() throws SQLException {
    // Arrange
    when(connection.prepareStatement(anyString())).thenReturn(statement);
    when(statement.executeQuery()).thenThrow(new SQLException("Execute query failed"));

    // Act & Assert
    assertThrows(PersistenceException.class, () -> repository.getByEmail(userEmail));
  }

  @Test
  @DisplayName("Debe lanzar PersistenceException cuando falla el cierre del recurso en búsqueda por email")
  void shouldThrowPersistenceExceptionWhenGetByEmailStatementCloseFails() throws SQLException {
    // Arrange
    when(connection.prepareStatement(anyString())).thenReturn(statement);
    when(statement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(false);
    doThrow(new SQLException("Close failed")).when(statement).close();

    // Act & Assert
    assertThrows(PersistenceException.class, () -> repository.getByEmail(userEmail));
  }

  @Test
  @DisplayName("Debe retornar una lista con todos los usuarios presentes en la base de datos")
  void shouldReturnAllUsers() throws SQLException {
    // Arrange
    configureStatementAndResultSet();
    when(resultSet.next()).thenReturn(true, false);
    configureResultSetRow();

    // Act
    final List<UserModel> result = repository.getAll();

    // Assert
    assertAll(
        "Verificación de obtención de todos los usuarios",
        () -> assertEquals(1, result.size()),
        () -> assertEquals(ID, result.get(0).getId().value()));
  }

  @Test
  @DisplayName("Debe lanzar PersistenceException cuando la consulta de todos los usuarios falla")
  void shouldThrowPersistenceExceptionOnGetAllFailure() throws SQLException {
    // Arrange
    when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Query failed"));

    // Act & Assert
    assertThrows(PersistenceException.class, () -> repository.getAll());
  }

  @Test
  @DisplayName("Debe ejecutar el DELETE sin lanzar excepciones cuando el proceso es exitoso")
  void shouldDeleteUserWithoutThrowing() throws SQLException {
    // Arrange
    when(connection.prepareStatement(anyString())).thenReturn(statement);

    // Act & Assert
    assertDoesNotThrow(() -> repository.delete(userId));
  }

  @Test
  @DisplayName("Debe lanzar PersistenceException cuando el DELETE falla por error de SQL")
  void shouldThrowPersistenceExceptionWhenDeleteFails() throws SQLException {
    // Arrange
    when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Delete failed"));

    // Act & Assert
    assertThrows(PersistenceException.class, () -> repository.delete(userId));
  }
}
