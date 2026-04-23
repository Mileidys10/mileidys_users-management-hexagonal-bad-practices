package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler;

import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.UserResponsePrinter;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.controller.UserController;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.util.List;
import java.util.logging.Level;

@Log
@RequiredArgsConstructor
public final class ListUsersHandler implements OperationHandler {

    private static final String LIST_USERS_LOG = "All users list was requested.";

    private final UserController userController;
    private final UserResponsePrinter printer;

    @Override
    public void handle() {
        log.log(Level.INFO, LIST_USERS_LOG);

        final List<UserResponse> users = userController.listAllUsers();

        printer.printList(users);
    }
}