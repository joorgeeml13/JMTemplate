package base.template.application.ports.in.auth.usecase;

import base.template.application.ports.in.auth.command.RegisterCommand;

public interface RegisterAccountUseCase {
    void execute(RegisterCommand command);
}
