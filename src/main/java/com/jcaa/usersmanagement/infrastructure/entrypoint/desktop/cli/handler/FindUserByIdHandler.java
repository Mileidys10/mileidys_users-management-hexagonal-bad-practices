package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.handler;

import com.jcaa.usersmanagement.domain.valueobject.UserId;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.ConsoleIO;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.cli.io.UserResponsePrinter;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.controller.UserController;
import com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto.UserResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class FindUserByIdHandler implements OperationHandler {

  private final UserController userController;
  private final ConsoleIO console;
  private final UserResponsePrinter printer;

  @Override
  public void handle() {
    final String idInput = console.readRequired("User ID: ");
    final UserResponse user = userController.findUserById(new UserId(idInput));
    printer.print(user);
  }
}