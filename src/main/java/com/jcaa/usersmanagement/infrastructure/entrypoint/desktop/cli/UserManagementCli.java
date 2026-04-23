package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli;

import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler.*;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.ConsoleIO;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.UserResponsePrinter;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.menu.MenuOption;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.controller.UserController;
import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class UserManagementCli {

    private static final String BANNER =
            """
            ==========================================
                 Users Management System
            ==========================================""";

    private static final String MENU_BORDER = "  ==========================================";
    private static final String MAIN_MENU_TITLE = "    Main Menu";
    private static final String INVALID_OPTION_MSG = "  Invalid option. Please try again.";
    private static final String GOODBYE_MSG = "\n  Goodbye!\n";
    private static final String VALIDATION_ERROR_HEADER = "  Validation errors:";
    private static final String UNEXPECTED_ERROR_PREFIX = "  Unexpected error: ";

    private final UserController userController;
    private final ConsoleIO console;

    public void start() {
        console.println(BANNER);
        final UserResponsePrinter printer = new UserResponsePrinter(console);
        runLoop(buildHandlers(printer));
    }

    private void runLoop(final Map<MenuOption, OperationHandler> handlers) {
        boolean isRunning = true;
        while (isRunning) {
            printMenu();
            final int choice = console.readInt("\n  Option: ");
            final Optional<MenuOption> option = MenuOption.fromNumber(choice);

            if (option.isEmpty()) {
                console.println(INVALID_OPTION_MSG);
            } else if (option.get() == MenuOption.EXIT) {
                console.println(GOODBYE_MSG);
                isRunning = false;
            } else {
                executeHandler(handlers, option.get());
            }
        }
    }

    private void executeHandler(
            final Map<MenuOption, OperationHandler> handlers, final MenuOption option) {
        try {
            handlers.get(option).handle();
        } catch (final ConstraintViolationException exception) {
            console.println(VALIDATION_ERROR_HEADER);
            exception.getConstraintViolations()
                    .forEach(violation -> console.println("    - " + violation.getMessage()));
        } catch (final RuntimeException exception) {
            console.println(UNEXPECTED_ERROR_PREFIX + exception.getMessage());
        }
    }

    private Map<MenuOption, OperationHandler> buildHandlers(final UserResponsePrinter printer) {
        return Map.of(
                MenuOption.LIST_USERS,  new ListUsersHandler(userController, printer),
                MenuOption.FIND_USER,   new FindUserByIdHandler(userController, console, printer),
                MenuOption.CREATE_USER, new CreateUserHandler(userController, console, printer),
                MenuOption.UPDATE_USER, new UpdateUserHandler(userController, console, printer),
                MenuOption.DELETE_USER, new DeleteUserHandler(userController, console),
                MenuOption.LOGIN,       new LoginHandler(userController, console, printer));
    }

    private void printMenu() {
        console.println();

        console.println(MENU_BORDER);
        console.println(MAIN_MENU_TITLE);
        console.println(MENU_BORDER);


        for (final MenuOption option : MenuOption.values()) {
            console.printf("    [%d] %s%n", option.getNumber(), option.getDescription());
        }

        console.println(MENU_BORDER);
    }
}