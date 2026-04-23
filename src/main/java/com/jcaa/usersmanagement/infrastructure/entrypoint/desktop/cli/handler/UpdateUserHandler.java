package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler;

import com.jcaa.usersmanagement.domain.exception.UserNotFoundException;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.ConsoleIO;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.UserResponsePrinter;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.controller.UserController;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.UpdateUserRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.util.logging.Level;

@Log
@RequiredArgsConstructor
public final class UpdateUserHandler implements OperationHandler {


    private static final String UPDATE_SUCCESS = "\n  User updated successfully.";
    private static final String NOT_FOUND_PREFIX = "  Not found: ";
    private static final String UPDATE_NOT_FOUND_LOG = "Attempted to update a user that does not exist.";

    private final UserController userController;
    private final ConsoleIO console;
    private final UserResponsePrinter printer;

    @Override
    public void handle() {

        final String id       = console.readRequired("User ID                                       : ");
        final String name     = console.readRequired("New name                                      : ");
        final String email    = console.readRequired("New email                                     : ");
        final String password = console.readOptional("New password (leave blank to keep current)    : ");
        final String role     = console.readRequired("Role   (ADMIN / MEMBER / REVIEWER)            : ");
        final String status   = console.readRequired("Status (ACTIVE / INACTIVE / PENDING / BLOCKED): ");

        try {

            final UserResponse updatedUser = userController.updateUser(
                    new UpdateUserRequest(
                            id,
                            name,
                            email,
                            password.isBlank() ? null : password,
                            role,
                            status));

            console.println(UPDATE_SUCCESS);
            printer.print(updatedUser);

        } catch (final UserNotFoundException exception) {

            log.log(Level.WARNING, UPDATE_NOT_FOUND_LOG);


            console.println(NOT_FOUND_PREFIX + exception.getMessage());
        }
    }
}