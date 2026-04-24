package com.jcaa.usersmanagement.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.jcaa.usersmanagement.application.port.out.GetAllUsersPort;
import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests para GetAllUsersService.
 *
 * <p>Cubre la obtención de todos los usuarios registrados, asegurando que se
 * retorne una lista poblada o una lista vacía según corresponda.
 */
@DisplayName("GetAllUsersService")
@ExtendWith(MockitoExtension.class)
class GetAllUsersServiceTest {

  @Mock private GetAllUsersPort getAllUsersPort;

  private GetAllUsersService service;

  @BeforeEach
  void setUp() {
    service = new GetAllUsersService(getAllUsersPort);
  }

  @Test
  @DisplayName("Debe retornar la lista de usuarios del puerto cuando existen registros")
  void shouldReturnUsersFromPort() {
    // Arrange
    final UserModel user =
        new UserModel(
            new UserId("u-001"),
            new UserName("John Arrieta"),
            new UserEmail("john@example.com"),
            UserPassword.fromHash("$2a$12$abcdefghijklmnopqrstuO"),
            UserRole.ADMIN,
            UserStatus.ACTIVE);
    when(getAllUsersPort.getAll()).thenReturn(List.of(user));

    // Act
    final List<UserModel> result = service.execute();

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertSame(user, result.get(0));
  }

  @Test
  @DisplayName("Debe retornar una lista vacía cuando no existen usuarios")
  void shouldReturnEmptyListWhenNoUsers() {
    // Arrange
    when(getAllUsersPort.getAll()).thenReturn(List.of());

    // Act
    final List<UserModel> result = service.execute();

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty(), "Debe retornar una lista vacía en lugar de null");
  }
}
