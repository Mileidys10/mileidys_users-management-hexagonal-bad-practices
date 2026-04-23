package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.mapper;

import com.jcaa.usersmanagement.application.service.dto.command.*;
import com.jcaa.usersmanagement.application.service.dto.query.GetUserByIdQuery;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.*;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


@UtilityClass
public final class UserDesktopMapper {
    private static final String INVALID_ID_MSG="ID inválido: no puede estar vacío";
    public static CreateUserCommand toCreateCommand(final CreateUserRequest request) {
        return new CreateUserCommand(
                request.id(), request.name(), request.email(), request.password(), request.role());
    }

    public static UpdateUserCommand toUpdateCommand(final UpdateUserRequest request) {
        return new UpdateUserCommand(
                request.id(),
                request.name(),
                request.email(),
                request.password(),
                request.role(),
                request.status());
    }

    public static DeleteUserCommand toDeleteCommand(final String id) {
        requireValidId(id);
        return new DeleteUserCommand(id);
    }

    public static GetUserByIdQuery toGetByIdQuery(final String id) {
        requireValidId(id);
        return new GetUserByIdQuery(id);
    }

    public static LoginCommand toLoginCommand(final LoginRequest request) {
        return new LoginCommand(request.email(), request.password());
    }

    public static UserResponse toResponse(final UserModel user) {

        return new UserResponse(
                user.getId().value(),
                user.getName().value(),
                user.getEmail().value(),
                user.getRole().name(),
                user.getStatus().name());
    }

    public static List<UserResponse> toResponseList(final List<UserModel> users) {

        if (Objects.isNull(users)) {
            return Collections.emptyList();
        }
        return users.stream()
                .map(UserDesktopMapper::toResponse)
                .toList();
    }


    private static void requireValidId(final String id) {
        if (Objects.isNull(id) || id.isBlank()) {
            throw new IllegalArgumentException(INVALID_ID_MSG);
        }
    }
}