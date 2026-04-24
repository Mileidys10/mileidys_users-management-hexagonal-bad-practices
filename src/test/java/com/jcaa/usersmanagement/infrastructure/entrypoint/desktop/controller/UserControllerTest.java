package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jcaa.usersmanagement.application.port.in.CreateUserUseCase;
import com.jcaa.usersmanagement.application.port.in.DeleteUserUseCase;
import com.jcaa.usersmanagement.application.port.in.GetAllUsersUseCase;
import com.jcaa.usersmanagement.application.port.in.GetUserByIdUseCase;
import com.jcaa.usersmanagement.application.port.in.LoginUseCase;
import com.jcaa.usersmanagement.application.port.in.UpdateUserUseCase;
import com.jcaa.usersmanagement.application.service.dto.command.CreateUserCommand;
import com.jcaa.usersmanagement.application.service.dto.command.DeleteUserCommand;
import com.jcaa.usersmanagement.application.service.dto.command.LoginCommand;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateUserCommand;
import com.jcaa.usersmanagement.application.service.dto.query.GetUserByIdQuery;
import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.exception.InvalidCredentialsException;
import com.jcaa.usersmanagement.domain.exception.UserAlreadyExistsException;
import com.jcaa.usersmanagement.domain.exception.UserNotFoundException;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.domain.valueobject.UserName;
import com.jcaa.usersmanagement.domain.valueobject.UserPassword;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.CreateUserRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.LoginRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.UpdateUserRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.UserResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests para UserController.
 *
 * <p>
 * Verifica la correcta delegación del controlador hacia los puertos de los
 * casos de uso,
 * asegurando el mapeo adecuado entre DTOs de entrada, comandos de aplicación y
 * respuestas de
 * salida.
 */
@DisplayName("UserController")
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private static final String BCRYPT_HASH = "$2a$12$abcdefghijklmnopqrstabcdefghijklmnñopqrstuvwxyzabcdefgh";

    @Mock
    private CreateUserUseCase createUserUseCase;
    @Mock
    private UpdateUserUseCase updateUserUseCase;
    @Mock
    private DeleteUserUseCase deleteUserUseCase;
    @Mock
    private GetUserByIdUseCase getUserByIdUseCase;
    @Mock
    private GetAllUsersUseCase getAllUsersUseCase;
    @Mock
    private LoginUseCase loginUseCase;

    private UserController controller;

    private static UserModel buildUser(
            final String id,
            final String name,
            final String email,
            final UserRole role,
            final UserStatus status) {
        return new UserModel(
                new UserId(id),
                new UserName(name),
                new UserEmail(email),
                UserPassword.fromHash(BCRYPT_HASH),
                role,
                status);
    }

    @BeforeEach
    void setUp() {
        controller = new UserController(
                createUserUseCase,
                updateUserUseCase,
                deleteUserUseCase,
                getUserByIdUseCase,
                getAllUsersUseCase,
                loginUseCase);
    }

    @Test
    @DisplayName("Debe retornar una lista de respuestas de usuario correctamente mapeada cuando existen usuarios")
    void shouldReturnMappedResponseListWhenUsersExist() {
        // Arrange
        final UserModel user = buildUser("u-001", "Alice Smith", "alice@example.com", UserRole.ADMIN,
                UserStatus.ACTIVE);
        when(getAllUsersUseCase.execute()).thenReturn(List.of(user));

        // Act
        final List<UserResponse> result = controller.listAllUsers();

        // Assert
        assertAll(
                "Verificación de mapeo de lista de usuarios",
                () -> assertEquals(1, result.size()),
                () -> assertEquals("u-001", result.get(0).id()),
                () -> assertEquals("Alice Smith", result.get(0).name()),
                () -> assertEquals("alice@example.com", result.get(0).email()),
                () -> assertEquals("ADMIN", result.get(0).role()),
                () -> assertEquals("ACTIVE", result.get(0).status()));
        verify(getAllUsersUseCase).execute();
    }

    @Test
    @DisplayName("Debe retornar una lista vacía cuando el caso de uso no encuentra usuarios")
    void shouldReturnEmptyListWhenNoUsersExist() {
        // Arrange
        when(getAllUsersUseCase.execute()).thenReturn(List.of());

        // Act
        final List<UserResponse> result = controller.listAllUsers();

        // Assert
        assertTrue(result.isEmpty());
        verify(getAllUsersUseCase).execute();
    }

    @Test
    @DisplayName("Debe retornar la respuesta de usuario mapeada cuando se encuentra el usuario por ID")
    void shouldReturnMappedResponseWhenUserExists() {
        // Arrange
        final UserModel user = buildUser("u-002", "Bob Jones", "bob@example.com", UserRole.MEMBER, UserStatus.ACTIVE);
        when(getUserByIdUseCase.execute(new GetUserByIdQuery("u-002"))).thenReturn(user);

        // Act
        final UserResponse result = controller.findUserById("u-002");

        // Assert
        assertAll(
                "Verificación de mapeo de respuesta por ID",
                () -> assertEquals("u-002", result.id()),
                () -> assertEquals("Bob Jones", result.name()),
                () -> assertEquals("bob@example.com", result.email()),
                () -> assertEquals("MEMBER", result.role()),
                () -> assertEquals("ACTIVE", result.status()));
    }

    @Test
    @DisplayName("Debe propagar UserNotFoundException cuando el caso de uso no encuentra al usuario")
    void shouldPropagateUserNotFoundExceptionWhenUserDoesNotExist() {
        // Arrange
        when(getUserByIdUseCase.execute(new GetUserByIdQuery("u-999")))
                .thenThrow(UserNotFoundException.becauseIdWasNotFound("u-999"));

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> controller.findUserById("u-999"));
    }

    @Test
    @DisplayName("Debe delegar el comando de creación correctamente y retornar la respuesta mapeada")
    void shouldDelegateCorrectCommandAndReturnMappedResponseWhenCreationSucceeds() {
        // Arrange
        final CreateUserRequest request = new CreateUserRequest("u-003", "Carol White", "carol@example.com", "Pass1234",
                "REVIEWER");
        final UserModel createdUser = buildUser(
                "u-003", "Carol White", "carol@example.com", UserRole.REVIEWER, UserStatus.PENDING);
        final ArgumentCaptor<CreateUserCommand> captor = ArgumentCaptor.forClass(CreateUserCommand.class);
        when(createUserUseCase.execute(captor.capture())).thenReturn(createdUser);

        // Act
        final UserResponse result = controller.createUser(request);

        // Assert
        assertAll(
                "Verificación de delegación de creación y mapeo de respuesta",
                () -> assertEquals("u-003", captor.getValue().id()),
                () -> assertEquals("Carol White", captor.getValue().name()),
                () -> assertEquals("carol@example.com", captor.getValue().email()),
                () -> assertEquals("Pass1234", captor.getValue().password()),
                () -> assertEquals("REVIEWER", captor.getValue().role()),
                () -> assertEquals("u-003", result.id()),
                () -> assertEquals("PENDING", result.status()));
    }

    @Test
    @DisplayName("Debe propagar UserAlreadyExistsException cuando el email ya está registrado")
    void shouldPropagateUserAlreadyExistsExceptionWhenEmailIsDuplicated() {
        // Arrange
        final CreateUserRequest request = new CreateUserRequest("u-004", "Dave Brown", "dave@example.com", "Pass5678",
                "MEMBER");
        when(createUserUseCase.execute(any()))
                .thenThrow(UserAlreadyExistsException.becauseEmailAlreadyExists("dave@example.com"));

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> controller.createUser(request));
    }

    @Test
    @DisplayName("Debe delegar el comando de actualización correctamente y retornar la respuesta mapeada")
    void shouldDelegateCorrectCommandAndReturnMappedResponseWhenUpdateSucceeds() {
        // Arrange
        final UpdateUserRequest request = new UpdateUserRequest(
                "u-005", "Eve Martinez", "eve@example.com", "NewPass9!", "ADMIN", "ACTIVE");
        final UserModel updatedUser = buildUser("u-005", "Eve Martinez", "eve@example.com", UserRole.ADMIN,
                UserStatus.ACTIVE);

        final ArgumentCaptor<UpdateUserCommand> captor = ArgumentCaptor.forClass(UpdateUserCommand.class);

        when(updateUserUseCase.execute(captor.capture())).thenReturn(updatedUser);


        when(getUserByIdUseCase.execute(any(GetUserByIdQuery.class))).thenReturn(updatedUser);

        // Act
        final UserResponse result = controller.updateUser(request);

        // Assert
        assertAll(
                "Verificación de delegación de actualización y mapeo de respuesta",
                () -> assertEquals("u-005", captor.getValue().id()),
                () -> assertEquals("Eve Martinez", captor.getValue().name()),
                () -> assertEquals("eve@example.com", captor.getValue().email()),
                () -> assertEquals("u-005", result.id()),
                () -> assertEquals("ADMIN", result.role()));
    }

    @Test
    @DisplayName("Debe propagar UserNotFoundException en actualización cuando el usuario no existe")
    void shouldPropagateUserNotFoundExceptionWhenUserDoesNotExistOnUpdate() {
        // Arrange
        final UpdateUserRequest request = new UpdateUserRequest(
                "u-999", "Ghost User", "ghost@example.com", "Pass9999!", "MEMBER", "INACTIVE");
        when(updateUserUseCase.execute(any()))
                .thenThrow(UserNotFoundException.becauseIdWasNotFound("u-999"));

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> controller.updateUser(request));
    }

    @Test
    @DisplayName("Debe delegar el comando de eliminación con el ID correcto al caso de uso")
    void shouldDelegateDeleteCommandWithCorrectId() {
        // Arrange
        final ArgumentCaptor<DeleteUserCommand> captor = ArgumentCaptor.forClass(DeleteUserCommand.class);
        doNothing().when(deleteUserUseCase).execute(captor.capture());

        // Act
        controller.deleteUser("u-006");

        // Assert
        assertEquals("u-006", captor.getValue().id());
    }

    @Test
    @DisplayName("Debe propagar UserNotFoundException en eliminación cuando el usuario no existe")
    void shouldPropagateUserNotFoundExceptionWhenUserDoesNotExistOnDelete() {
        // Arrange
        doThrow(UserNotFoundException.becauseIdWasNotFound("u-999"))
                .when(deleteUserUseCase)
                .execute(any());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> controller.deleteUser("u-999"));
    }

    @Test
    @DisplayName("Debe delegar el comando de login correctamente y retornar la respuesta mapeada")
    void shouldDelegateCorrectCommandAndReturnMappedResponseWhenCredentialsAreValid() {
        // Arrange
        final LoginRequest request = new LoginRequest("frank@example.com", "Pass1234!");
        final UserModel loggedUser = buildUser("u-007", "Frank Green", "frank@example.com", UserRole.MEMBER,
                UserStatus.ACTIVE);
        final ArgumentCaptor<LoginCommand> captor = ArgumentCaptor.forClass(LoginCommand.class);
        when(loginUseCase.execute(captor.capture())).thenReturn(loggedUser);

        // Act
        final UserResponse result = controller.login(request);

        // Assert
        assertAll(
                "Verificación de delegación de login y mapeo de respuesta",
                () -> assertEquals("frank@example.com", captor.getValue().email()),
                () -> assertEquals("Pass1234!", captor.getValue().password()),
                () -> assertEquals("u-007", result.id()),
                () -> assertEquals("frank@example.com", result.email()),
                () -> assertEquals("ACTIVE", result.status()));
    }

    @Test
    @DisplayName("Debe propagar InvalidCredentialsException cuando las credenciales son inválidas")
    void shouldPropagateInvalidCredentialsExceptionWhenCredentialsAreInvalid() {
        // Arrange
        final LoginRequest request = new LoginRequest("frank@example.com", "WrongPass1");
        when(loginUseCase.execute(any()))
                .thenThrow(InvalidCredentialsException.becauseCredentialsAreInvalid());

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> controller.login(request));
    }
}
