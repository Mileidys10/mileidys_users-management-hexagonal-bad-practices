package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler;

import com.jcaa.usersmanagement.domain.exception.UserNotFoundException;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.ConsoleIO;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.controller.UserController;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log; // Agregamos el log

import java.util.logging.Level;

@Log
@RequiredArgsConstructor
public final class DeleteUserHandler implements OperationHandler {


    private static final String DELETE_SUCCESS = "  User deleted successfully.";
    private static final String NOT_FOUND_PREFIX = "  Not found: ";
    private static final String DELETE_NOT_FOUND_LOG = "Attempted to delete a user that does not exist.";

    private final UserController userController;
    private final ConsoleIO console;

    @Override
    public void handle() {
        final String id = console.readRequired("User ID to delete: ");

        try {
            userController.deleteUser(id);
            console.println(DELETE_SUCCESS);

        } catch (final UserNotFoundException exception) {

            log.log(Level.WARNING, DELETE_NOT_FOUND_LOG);


            console.println(NOT_FOUND_PREFIX + exception.getMessage());
        }
    }
}