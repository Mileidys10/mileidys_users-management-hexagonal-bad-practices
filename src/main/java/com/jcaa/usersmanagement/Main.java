package com.jcaa.usersmanagement;

import com.jcaa.usersmanagement.infrastructure.config.DependencyContainer;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.UserManagementCli;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.ConsoleIO;
import lombok.extern.java.Log;

import java.util.Scanner;


@Log
public final class Main {
    private static final String FAILED_START_APPLICATION_MSG = "Application failed to start: ";

    public static void main(final String[] args) {
        log.info("Starting Users Management System...");

        try (final Scanner scanner = new Scanner(System.in)) {
            final UserManagementCli cli = bootstrapApplication(scanner);
            cli.start();
        } catch (final Exception e) {
            log.severe(FAILED_START_APPLICATION_MSG + e.getMessage());
        }
    }


    private static UserManagementCli bootstrapApplication(final Scanner scanner) {
        final DependencyContainer container = new DependencyContainer();
        final ConsoleIO consoleIO = new ConsoleIO(scanner, System.out);

        return new UserManagementCli(container.userController(), consoleIO);
    }
}