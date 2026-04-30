package base.template.application.ports.in.auth.usecase;

import base.template.application.ports.in.auth.command.LoginCommand;
import base.template.application.security.AuthTokens;

public interface LoginAccountUseCase {
     AuthTokens execute(LoginCommand command);
}
