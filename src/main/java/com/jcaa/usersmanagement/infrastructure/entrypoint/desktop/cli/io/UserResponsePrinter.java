package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io;

import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.UserResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class UserResponsePrinter {

    private static final String SEPARATOR = "-".repeat(52);
    private static final String ROW_FORMAT = "  %-10s : %s%n";
    private static final String NO_USERS_FOUND = "  No users found.";


    private static final Map<String, String> STATUS_LABELS = Map.of(
            "ACTIVE",   "Activo",
            "INACTIVE", "Inactivo",
            "PENDING",  "Pendiente de activación",
            "BLOCKED",  "Bloqueado",
            "DELETED",  "Eliminado"
    );
    private static final String UNKNOWN_STATUS = "Estado desconocido";

    private final ConsoleIO console;

    public void print(final UserResponse response) {
        console.println(SEPARATOR);
        console.printf(ROW_FORMAT, "ID",     response.id());
        console.printf(ROW_FORMAT, "Name",   response.name());
        console.printf(ROW_FORMAT, "Email",  response.email());
        console.printf(ROW_FORMAT, "Role",   response.role());
        console.printf(ROW_FORMAT, "Status", getStatusLabel(response.status()));
        console.println(SEPARATOR);
    }

    public void printList(final List<UserResponse> users) {
        if (Objects.isNull(users) || users.isEmpty()) {
            console.println(NO_USERS_FOUND);
            return;
        }

        console.printf("%n  Total: %d user(s)%n", users.size());
        users.forEach(this::print);
    }

    public void printSummary(final List<UserResponse> users) {
        if (Objects.isNull(users) || users.isEmpty()) {
            console.println(NO_USERS_FOUND);
            return;
        }

        for (UserResponse user : users) {
            console.printf("  %s (%s)%n", user.name(), getStatusLabel(user.status()));
        }
    }

    private static String getStatusLabel(final String status) {
        return STATUS_LABELS.getOrDefault(status, UNKNOWN_STATUS);
    }
}