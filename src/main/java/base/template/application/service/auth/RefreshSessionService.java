package base.template.application.service.auth;


import java.time.Instant;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import base.template.application.exception.security.UnauthorizedException;
import base.template.application.ports.in.auth.command.RefreshCommand;
import base.template.application.ports.in.auth.usecase.RefreshSessionUseCase;
import base.template.application.ports.out.AccountRepositoryPort;
import base.template.application.ports.out.RefreshTokenRepositoryPort;
import base.template.application.security.AuthTokens;
import base.template.application.security.TokenUtils;
import base.template.domain.model.auth.Account;
import base.template.domain.model.auth.RefreshToken;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RefreshSessionService implements RefreshSessionUseCase{

    private final TokenUtils tokenUtils;
    private final AccountRepositoryPort accountRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;

    @Override
    @Transactional
    public AuthTokens execute(RefreshCommand command) {
        String rawToken = command.rawRefreshToken();

        RefreshToken currentToken = refreshTokenRepository.findByToken(rawToken)
            .orElseThrow(() -> new UnauthorizedException("error.auth.refresh.unfound"));

        if (currentToken.isRevoked()) {
            refreshTokenRepository.revokeAllByAccountId(currentToken.getAccount().getId());
            throw new UnauthorizedException("error.auth.security.breach_detected");
        }

        if (currentToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(currentToken);
            throw new UnauthorizedException("error.auth.refresh.expired");
        }

        AuthTokens newTokens = tokenUtils.issue(currentToken.getAccount());

        currentToken.revoke(newTokens.refreshToken());
        refreshTokenRepository.save(currentToken);

        RefreshToken newToken = RefreshToken.create(
            newTokens.refreshToken(), 
            currentToken.getAccount(), 
            604800000L 
        );
        refreshTokenRepository.save(newToken);

        return newTokens;
    }
}