package com.jcaa.usersmanagement.application.service;

import com.jcaa.usersmanagement.application.port.in.UpdateUserUseCase;
import com.jcaa.usersmanagement.application.port.out.GetUserByEmailPort;
import com.jcaa.usersmanagement.application.port.out.GetUserByIdPort;
import com.jcaa.usersmanagement.application.port.out.UpdateUserPort;
import com.jcaa.usersmanagement.application.service.dto.command.UpdateUserCommand;
import com.jcaa.usersmanagement.application.service.mapper.UserApplicationMapper;
import com.jcaa.usersmanagement.domain.exception.UserAlreadyExistsException;
import com.jcaa.usersmanagement.domain.exception.UserNotFoundException;
import com.jcaa.usersmanagement.domain.model.UserModel;
import com.jcaa.usersmanagement.domain.valueobject.UserEmail;
import com.jcaa.usersmanagement.domain.valueobject.UserId;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.util.Set;

@Log
@RequiredArgsConstructor
public final class UpdateUserService implements UpdateUserUseCase {

    private final UpdateUserPort updateUserPort;
    private final GetUserByIdPort getUserByIdPort;
    private final GetUserByEmailPort getUserByEmailPort;
    private final EmailNotificationService emailNotificationService;
    private final Validator validator;
    private static final String SUCCESFULL_UPDATE_USER = "User updated successfully";

    @Override
    public UserModel execute(final UpdateUserCommand command) {
        validateCommand(command);

        final UserId userId = new UserId(command.id());
        final UserModel current = findExistingUserOrFail(userId);

        validateEmailUniqueness(new UserEmail(command.email()), userId);

        final UserModel userToUpdate = UserApplicationMapper.fromUpdateCommandToModel(command, current.getPassword());
        final UserModel updatedUser = updateUserPort.update(userToUpdate);


        emailNotificationService.notifyUserUpdated(updatedUser);

        log.info(SUCCESFULL_UPDATE_USER);
        return updatedUser;
    }



    private void validateEmailUniqueness(final UserEmail newEmail, final UserId ownerId) {

        getUserByEmailPort.getByEmail(newEmail).ifPresent(userWithEmail -> {
            if (isEmailOwnedByAnotherUser(userWithEmail, ownerId)) {
                throw UserAlreadyExistsException.becauseEmailAlreadyExists(newEmail.value());
            }
        });
    }

    private boolean isEmailOwnedByAnotherUser(final UserModel userWithEmail, final UserId currentOwnerId) {
        return !userWithEmail.getId().equals(currentOwnerId);
    }

    private void validateCommand(final UpdateUserCommand command) {
        final Set<ConstraintViolation<UpdateUserCommand>> violations = validator.validate(command);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private UserModel findExistingUserOrFail(final UserId userId) {
        return getUserByIdPort.getById(userId)
                .orElseThrow(() -> UserNotFoundException.becauseIdWasNotFound(userId.value()));
    }
}