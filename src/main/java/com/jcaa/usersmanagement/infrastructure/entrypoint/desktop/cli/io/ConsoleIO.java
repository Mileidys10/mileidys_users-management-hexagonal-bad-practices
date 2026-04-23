package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io;

import java.io.PrintStream;
import java.util.Scanner;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ConsoleIO {


    private static final String ERROR_BLANK_VALUE = "  Value cannot be blank. Please try again.";
    private static final String ERROR_INVALID_NUMBER = "  Invalid input. Please enter a number.";

    private final Scanner scanner;
    private final PrintStream out;

    public String readRequired(final String prompt) {

        String userInput;
        do {
            out.print(prompt);
            userInput = scanner.nextLine().trim();

            if (userInput.isBlank()) {
                out.println(ERROR_BLANK_VALUE);
            }
        } while (userInput.isBlank());

        return userInput;
    }

    public String readOptional(final String prompt) {
        out.print(prompt);
        return scanner.nextLine().trim();
    }

    public int readInt(final String prompt) {
        while (true) {
            out.print(prompt);

            final String userInput = scanner.nextLine().trim();
            try {
                return Integer.parseInt(userInput);
            } catch (final NumberFormatException ignored) {
                out.println(ERROR_INVALID_NUMBER);
            }
        }
    }

    public void println(final String message) { out.println(message); }
    public void println() { out.println(); }
    public void printf(final String format, final Object... args) { out.printf(format, args); }
}