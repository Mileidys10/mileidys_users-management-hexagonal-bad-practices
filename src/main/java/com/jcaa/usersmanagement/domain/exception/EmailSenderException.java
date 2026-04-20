package com.jcaa.usersmanagement.domain.exception;


public final class EmailSenderException extends DomainException {


    private static final String SMTP_FAILURE_MESSAGE = "No se pudo enviar el correo a '%s'. Error SMTP: %s";
    private static final String GENERAL_SEND_FAILURE_MESSAGE = "La notificación por correo no pudo ser enviada.";

    private EmailSenderException(final String message) {
        super(message);
    }

    private EmailSenderException(final String message, final Throwable cause) {
        super(message, cause);
    }


    public static EmailSenderException becauseSmtpFailed(
            final String destinationEmail, final String smtpError) {
        return new EmailSenderException(
                String.format(SMTP_FAILURE_MESSAGE, destinationEmail, smtpError));
    }


    public static EmailSenderException becauseSendFailed(final Throwable cause) {
        return new EmailSenderException(GENERAL_SEND_FAILURE_MESSAGE, cause);
    }
}

