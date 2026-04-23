package com.jcaa.usersmanagement.application.service.mapper;

import com.jcaa.usersmanagement.application.service.dto.command.*;
import com.jcaa.usersmanagement.application.service.dto.query.GetUserByIdQuery;
import com.jcaa.usersmanagement.domain.enums.UserRole;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.*;
import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.Optional;

@UtilityClass
public class UserApplicationMapper {

    private static final String EMPTY_ROLE_NAME_MSG= "Role name cannot be empty";
    private static final String UNKNOW_ROLE_MSG= "Unknown role: ";
    public static UserModel fromCreateCommandToModel(final CreateUserCommand command) {
        return UserModel.create(
                new UserId(command.id()),
                new UserName(command.name()),
                new UserEmail(command.email()),
                UserPassword.fromPlainText(command.password()),
                UserRole.fromString(command.role()));
    }

    public static UserModel fromUpdateCommandToModel(
            final UpdateUserCommand command, final UserPassword currentPassword) {

        final UserPassword passwordToUse = (Objects.isNull(command.password()) || command.password().isBlank())
                ? currentPassword
                : UserPassword.fromPlainText(command.password());

        return new UserModel(
                new UserId(command.id()),
                new UserName(command.name()),
                new UserEmail(command.email()),
                passwordToUse,
                UserRole.fromString(command.role()),
                UserStatus.fromString(command.status()));
    }

    public static UserId fromGetUserByIdQueryToUserId(final GetUserByIdQuery query) {
        return new UserId(query.id());
    }

    public static UserId fromDeleteCommandToUserId(final DeleteUserCommand command) {
        return new UserId(command.id());
    }


    public static int getRoleCode(final String roleName) {
        if (Objects.isNull(roleName) || roleName.isBlank()) {
            throw new IllegalArgumentException(EMPTY_ROLE_NAME_MSG);
        }

        return switch (roleName.toUpperCase()) {
            case "ADMIN"    -> 1;
            case "MEMBER"   -> 2;
            case "REVIEWER" -> 3;
            default -> throw new IllegalArgumentException(UNKNOW_ROLE_MSG + roleName);
        };
    }
}