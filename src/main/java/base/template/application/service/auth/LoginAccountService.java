package base.template.application.service.auth;

import java.time.Instant;

import org.springframework.transaction.annotation.Transactional;

import base.template.application.ports.in.auth.command.LoginCommand;
import base.template.application.ports.in.auth.usecase.LoginAccountUseCase;
import base.template.application.ports.out.AccountRepositoryPort;
import base.template.application.ports.out.RefreshTokenRepositoryPort;
import base.template.application.security.AuthTokens;
import base.template.application.security.TokenUtils;
import base.template.domain.exception.auth.InvalidCredentialsException;
import base.template.domain.model.auth.Account;
import base.template.domain.model.auth.Email;
import base.template.domain.model.auth.RefreshToken;
import base.template.domain.model.auth.RawPassword;
import base.template.domain.service.PasswordHasher;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginAccountService implements LoginAccountUseCase{

    private final AccountRepositoryPort accountRepository;
    private final PasswordHasher passwordHasher;
    private final TokenUtils tokenIssuer;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final long refreshTokenExpirationMs;

    @Override
    @Transactional
    public AuthTokens execute(LoginCommand command) {
        Email email = new Email(command.email());
        RawPassword rawPassword = new RawPassword(command.password());

        Account account = accountRepository.findByEmail(email).orElseThrow(InvalidCredentialsException::new);

        if(!passwordHasher.matches(rawPassword, account.getPassword())) throw new InvalidCredentialsException();

        refreshTokenRepository.deleteExpiredTokens(Instant.now());
        refreshTokenRepository.revokeAllByAccountId(account.getId());

        AuthTokens tokens = tokenIssuer.issue(account);

        RefreshToken refreshToken = RefreshToken.create(tokens.refreshToken(), account, refreshTokenExpirationMs);
        refreshTokenRepository.save(refreshToken);

        return tokens;
    }
    
}
