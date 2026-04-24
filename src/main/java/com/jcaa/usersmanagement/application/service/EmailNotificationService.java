package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.out.EmailSenderPort;
import com.jcaa.usersmanagement.domain.exception.EmailSenderException;
import com.jcaa.usersmanagement.domain.model.EmailDestinationModel;
import com.jcaa.usersmanagement.domain.model.UserModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

@Log
@RequiredArgsConstructor
public final class EmailNotificationService {

    private static final String SUBJECT_CREATED = "Tu cuenta ha sido creada — Gestión de Usuarios";
    private static final String SUBJECT_UPDATED = "Tu cuenta ha sido actualizada — Gestión de Usuarios";
    private static final String TEMPLATE_CREATED = "user-created.html";
    private static final String TEMPLATE_UPDATED = "user-updated.html";
private static final String FAILED_SEND_EMAIL_MSG = "Failed to send email notification to: {0}";
    private final EmailSenderPort emailSenderPort;

    public void notifyUserCreated(final UserModel user, final String plainPassword) {
        final Map<String, String> tokens = buildBaseTokens(user);
        tokens.put("password", plainPassword);

        sendNotification(user, SUBJECT_CREATED, TEMPLATE_CREATED, tokens);
    }

    public void notifyUserUpdated(final UserModel user) {
        final Map<String, String> tokens = buildBaseTokens(user);
        tokens.put("status", user.getStatus().name());

        sendNotification(user, SUBJECT_UPDATED, TEMPLATE_UPDATED, tokens);
    }



    private void sendNotification(
            final UserModel user,
            final String subject,
            final String templateName,
            final Map<String, String> tokens) {

        final String htmlBody = processTemplate(templateName, tokens);
        final EmailDestinationModel destination = buildDestination(user, subject, htmlBody);

        try {
            emailSenderPort.send(destination);
        } catch (final EmailSenderException e) {
            log.log(Level.SEVERE, FAILED_SEND_EMAIL_MSG, user.getEmail().value());
            throw e;
        }
    }


    private String processTemplate(final String templateName, final Map<String, String> tokens) {
        final String templateContent = loadTemplate(templateName);
        return renderTemplate(templateContent, tokens);
    }

    private Map<String, String> buildBaseTokens(final UserModel user) {
        final Map<String, String> tokens = new HashMap<>();
        tokens.put("name", user.getName().value());
        tokens.put("email", user.getEmail().value());
        tokens.put("role", user.getRole().name());
        return tokens;
    }

    private static String renderTemplate(final String template, final Map<String, String> values) {

        String result = template;
        for (final Map.Entry<String, String> entry : values.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }

    private String loadTemplate(final String templateName) {
        final String path = "/templates/" + templateName;
        try (InputStream inputStream = getClass().getResourceAsStream(path)) {
            if (Objects.isNull(inputStream)) {
                throw new IllegalStateException("Template not found: " + path);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw EmailSenderException.becauseSendFailed(e);
        }
    }

    private static EmailDestinationModel buildDestination(
            final UserModel user, final String subject, final String body) {
        return new EmailDestinationModel(
                user.getEmail().value(), user.getName().value(), subject, body);
    }
}