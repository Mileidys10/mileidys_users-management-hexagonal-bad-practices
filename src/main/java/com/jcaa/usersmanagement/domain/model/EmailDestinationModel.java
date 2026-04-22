package com.jcaa.usersmanagement.domain.model;

import java.util.Objects;


public record EmailDestinationModel(
        String destinationEmail,
        String destinationName,
        String subject,
        String body
) {


    private static final String EMAIL_REQUIRED_MSG = "El email del destinatario es requerido.";
    private static final String NAME_REQUIRED_MSG  = "El nombre del destinatario es requerido.";
    private static final String SUBJECT_REQUIRED_MSG = "El asunto es requerido.";
    private static final String BODY_REQUIRED_MSG    = "El cuerpo del mensaje es requerido.";


    public EmailDestinationModel {
        validateNotBlank(destinationEmail, EMAIL_REQUIRED_MSG);
        validateNotBlank(destinationName, NAME_REQUIRED_MSG);
        validateNotBlank(subject, SUBJECT_REQUIRED_MSG);
        validateNotBlank(body, BODY_REQUIRED_MSG);
    }

    private  void validateNotBlank(final String value, final String errorMessage) {

        Objects.requireNonNull(value, errorMessage);


        if (value.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
