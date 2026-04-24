package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.in.LoginUseCase;
import com.jcaa.usersmanagement.application.port.out.GetUserByEmailPort;
import com.jcaa.usersmanagement.application.service.dto.command.LoginCommand;
import com.jcaa.usersmanagement.domain.enums.UserStatus;
import com.jcaa.usersmanagement.domain.exception.InvalidCredentialsException;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public final class LoginService implements LoginUseCase {

    private final GetUserByEmailPort getUserByEmailPort;
    private final Validator validator;

    @Override
    public UserModel execute(final LoginCommand command) {
        validateCommand(command);

        final UserEmail email = new UserEmail(command.email());

        return getAndValidateUser(email, command.password());
    }

    private UserModel getAndValidateUser(final UserEmail email, final String plainPassword) {
        final UserModel user = getUserByEmailPort.getByEmail(email)
                .orElseThrow(() -> InvalidCredentialsException.becauseCredentialsAreInvalid());

        if (!user.verifyPassword(plainPassword)) {
            throw InvalidCredentialsException.becauseCredentialsAreInvalid();
        }

        ensureUserIsActive(user);

        return user;
    }

    private void validateCommand(final LoginCommand command) {
        final Set<ConstraintViolation<LoginCommand>> violations = validator.validate(command);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }


    private void ensureUserIsActive(final UserModel user) {
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw InvalidCredentialsException.becauseUserIsNotActive();
        }
    }
}