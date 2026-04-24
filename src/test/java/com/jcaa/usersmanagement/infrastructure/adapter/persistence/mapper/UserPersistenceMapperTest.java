package com.jcaa.usersmanagement.infrastructure.adapter.persistence.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.dto.UserPersistenceDto;
import com.jcaa.usersmanagement.infrastructure.adapter.persistence.entity.UserEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests para UserPersistenceMapper.
 *
 * <p>Cubre el mapeo entre modelos de dominio, DTOs de persistencia y entidades,
 * así como la extracción de datos desde ResultSet de JDBC.
 */
@DisplayName("UserPersistenceMapper")
@ExtendWith(MockitoExtension.class)
class UserPersistenceMapperTest {

  private static final String ID = "u-001";
  private static final String NAME = "John Doe";
  private static final String EMAIL = "john@example.com";
  private static final String HASH = "$2a$12$abcdefghijklmnopqrstuO";
  private static final String ROLE = "ADMIN";
  private static final String STATUS = "ACTIVE";
  private static final String CREATED_AT = "2024-01-01 00:00:00";
  private static final String UPDATED_AT = "2024-01-02 00:00:00";

  @Mock private ResultSet resultSet;

  private UserModel userModel;
  private UserEntity userEntity;

  @BeforeEach
  void setUp() {
    userModel =
        new UserModel(
            new UserId(ID),
            new UserName(NAME),
            new UserEmail(EMAIL),
            UserPassword.fromHash(HASH),
            UserRole.ADMIN,
            UserStatus.ACTIVE);

    userEntity = new UserEntity(ID, NAME, EMAIL, HASH, ROLE, STATUS, CREATED_AT, UPDATED_AT);
  }

  @Test
  @DisplayName("Debe mapear todos los campos del modelo de dominio al DTO de persistencia")
  void shouldMapModelToDto() {
    // Act
    final UserPersistenceDto result = UserPersistenceMapper.fromModelToDto(userModel);

    // Assert
    assertAll(
        "Verificación de mapeo a DTO",
        () -> assertEquals(ID, result.id()),
        () -> assertEquals(NAME, result.name()),
        () -> assertEquals(EMAIL, result.email()),
        () -> assertEquals(HASH, result.password()),
        () -> assertEquals(ROLE, result.role()),
        () -> assertEquals(STATUS, result.status()),
        () -> assertNull(result.createdAt()),
        () -> assertNull(result.updatedAt()));
  }

  @Test
  @DisplayName("Debe mapear todos los campos de la entidad al modelo de dominio")
  void shouldMapEntityToModel() {
    // Act
    final UserModel result = UserPersistenceMapper.fromEntityToModel(userEntity);

    // Assert
    assertAll(
        "Verificación de mapeo a modelo",
        () -> assertEquals(ID, result.getId().value()),
        () -> assertEquals(NAME, result.getName().value()),
        () -> assertEquals(EMAIL, result.getEmail().value()),
        () -> assertEquals(UserRole.ADMIN, result.getRole()),
        () -> assertEquals(UserStatus.ACTIVE, result.getStatus()));
  }

  @Test
  @DisplayName("Debe leer todas las columnas del ResultSet y mapearlas a una entidad")
  void shouldReadAllColumnsFromResultSet() throws SQLException {
    // Arrange
    when(resultSet.getString("id")).thenReturn(ID);
    when(resultSet.getString("name")).thenReturn(NAME);
    when(resultSet.getString("email")).thenReturn(EMAIL);
    when(resultSet.getString("password")).thenReturn(HASH);
    when(resultSet.getString("role")).thenReturn(ROLE);
    when(resultSet.getString("status")).thenReturn(STATUS);
    when(resultSet.getString("created_at")).thenReturn(CREATED_AT);
    when(resultSet.getString("updated_at")).thenReturn(UPDATED_AT);

    // Act
    final UserEntity result = UserPersistenceMapper.fromResultSetToEntity(resultSet);

    // Assert
    assertAll(
        "Verificación de mapeo desde ResultSet",
        () -> assertEquals(ID, result.id()),
        () -> assertEquals(NAME, result.name()),
        () -> assertEquals(EMAIL, result.email()),
        () -> assertEquals(HASH, result.password()),
        () -> assertEquals(ROLE, result.role()),
        () -> assertEquals(STATUS, result.status()),
        () -> assertEquals(CREATED_AT, result.createdAt()),
        () -> assertEquals(UPDATED_AT, result.updatedAt()));
  }

  @Test
  @DisplayName("Debe propagar SQLException cuando falla la lectura del ResultSet")
  void shouldPropagateExceptionFromResultSet() throws SQLException {
    // Arrange
    when(resultSet.getString(anyString())).thenThrow(new SQLException("Column read failed"));

    // Act & Assert
    assertThrows(
        SQLException.class,
        () -> UserPersistenceMapper.fromResultSetToEntity(resultSet));
  }

  @Test
  @DisplayName("Debe retornar una lista vacía cuando el ResultSet no tiene filas")
  void shouldReturnEmptyListWhenResultSetIsEmpty() throws SQLException {
    // Arrange
    when(resultSet.next()).thenReturn(false);

    // Act
    final List<UserModel> result = UserPersistenceMapper.fromResultSetToModelList(resultSet);

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Debe retornar un modelo de dominio por cada fila presente en el ResultSet")
  void shouldReturnOneModelPerRow() throws SQLException {
    // Arrange
    when(resultSet.next()).thenReturn(true, true, false);
    when(resultSet.getString("id")).thenReturn(ID, "u-002");
    when(resultSet.getString("name")).thenReturn(NAME, "Jane Doe");
    when(resultSet.getString("email")).thenReturn(EMAIL, "jane@example.com");
    when(resultSet.getString("password")).thenReturn(HASH, HASH);
    when(resultSet.getString("role")).thenReturn(ROLE, "MEMBER");
    when(resultSet.getString("status")).thenReturn(STATUS, "PENDING");
    when(resultSet.getString("created_at")).thenReturn(CREATED_AT, CREATED_AT);
    when(resultSet.getString("updated_at")).thenReturn(UPDATED_AT, UPDATED_AT);

    // Act
    final List<UserModel> result = UserPersistenceMapper.fromResultSetToModelList(resultSet);

    // Assert
    assertEquals(2, result.size());
  }

  @Test
  @DisplayName("Debe propagar SQLException cuando falla la lectura de una fila durante la iteración")
  void shouldPropagateExceptionDuringIteration() throws SQLException {
    // Arrange
    when(resultSet.next()).thenReturn(true);
    when(resultSet.getString(anyString())).thenThrow(new SQLException("Row read failed"));

    // Act & Assert
    assertThrows(
        SQLException.class,
        () -> UserPersistenceMapper.fromResultSetToModelList(resultSet));
  }
}
