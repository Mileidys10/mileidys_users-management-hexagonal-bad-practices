package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler;

import com.jcaa.usersmanagement.domain.exception.UserNotFoundException;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.ConsoleIO;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.UserResponsePrinter;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.controller.UserController;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.util.logging.Level;

@Log
@RequiredArgsConstructor
public final class FindUserByIdHandler implements OperationHandler {

    private static final String ID_PROMPT = "User ID: ";
    private static final String NOT_FOUND_PREFIX = "  Not found: ";
    private static final String FIND_NOT_FOUND_LOG = "Search failed: User ID not found.";

    private final UserController userController;
    private final ConsoleIO console;
    private final UserResponsePrinter printer;

    @Override
    public void handle() {
        final String idInput = console.readRequired(ID_PROMPT);

        try {
            final UserResponse user = userController.findUserById(idInput);
            printer.print(user);

        } catch (final UserNotFoundException exception) {
            log.log(Level.INFO, FIND_NOT_FOUND_LOG);

            console.println(NOT_FOUND_PREFIX + exception.getMessage());
        }
    }
}