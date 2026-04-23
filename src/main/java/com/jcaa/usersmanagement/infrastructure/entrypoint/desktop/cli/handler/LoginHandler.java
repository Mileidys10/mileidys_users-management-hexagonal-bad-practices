package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler;

import com.jcaa.usersmanagement.domain.exception.InvalidCredentialsException;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.ConsoleIO;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.UserResponsePrinter;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.controller.UserController;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.LoginRequest;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.util.logging.Level;

@Log
@RequiredArgsConstructor
public final class LoginHandler implements OperationHandler {


    private static final String LOGIN_SUCCESS_MSG = "\n  Login successful. Welcome!";
    private static final String LOGIN_FAILED_LOG = "Failed login attempt detected.";
    private static final String ERROR_PREFIX = "  Error: ";

    private final UserController userController;
    private final ConsoleIO console;
    private final UserResponsePrinter printer;

    @Override
    public void handle() {
        final String email    = console.readRequired("Email   : ");
        final String password = console.readRequired("Password: ");

        try {
            final UserResponse user = userController.login(new LoginRequest(email, password));

            console.println(LOGIN_SUCCESS_MSG);
            printer.print(user);

        } catch (final InvalidCredentialsException exception) {

            log.log(Level.WARNING, LOGIN_FAILED_LOG);


            console.println(ERROR_PREFIX + exception.getMessage());
        }
    }
}