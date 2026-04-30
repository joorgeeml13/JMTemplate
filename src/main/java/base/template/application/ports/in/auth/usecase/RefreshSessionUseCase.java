package base.template.application.ports.in.auth.usecase;

import base.template.application.ports.in.auth.command.RefreshCommand;
import base.template.application.security.AuthTokens;

public interface RefreshSessionUseCase {
    AuthTokens execute(RefreshCommand command);
}
