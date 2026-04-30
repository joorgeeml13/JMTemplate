package base.template.application.service.auth;


import java.util.UUID;

import base.template.application.exception.security.UnauthorizedException;
import base.template.application.ports.in.auth.command.RefreshCommand;
import base.template.application.ports.in.auth.usecase.RefreshSessionUseCase;
import base.template.application.ports.out.AccountRepositoryPort;
import base.template.application.security.AuthTokens;
import base.template.application.security.TokenUtils;
import base.template.domain.model.auth.Account;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RefreshSessionService implements RefreshSessionUseCase{

    private final TokenUtils tokenUtils;
    private final AccountRepositoryPort accountRepository;

    @Override
    public AuthTokens execute(RefreshCommand command) {
        if(tokenUtils.isValidFormmat(command.rawRefreshToken())) throw new UnauthorizedException("error.auth.invalid.token.manipulado");

        String userId = tokenUtils.extractId(command.rawRefreshToken());
        if(userId == null || !tokenUtils.isTokenValid(command.rawRefreshToken(), userId))
            throw new UnauthorizedException("error.auth.invalid.token.user");

        Account account = accountRepository.findById(UUID.fromString(userId))
            .orElseThrow(() -> new UnauthorizedException("error.auth.refresh.unfound"));
            
        return tokenUtils.issue(account);
    }
    
}
