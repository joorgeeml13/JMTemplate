package base.template.infrastructure.config.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import base.template.application.ports.out.AccountRepositoryPort;
import base.template.application.ports.out.DomainEventPublisher;
import base.template.application.ports.out.RefreshTokenRepositoryPort;
import base.template.application.security.TokenUtils;
import base.template.application.service.auth.LoginAccountService;
import base.template.application.service.auth.RefreshSessionService;
import base.template.application.service.auth.RegisterAccountService;
import base.template.domain.service.PasswordHasher;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class UseCaseConfig {
    
    @Bean
    public RegisterAccountService registerAccountService(
        AccountRepositoryPort accountRepository,
        PasswordHasher passwordHasher,
        DomainEventPublisher eventPublisher
    ){return new RegisterAccountService(accountRepository, passwordHasher, eventPublisher);}

    @Bean
    public LoginAccountService loginAccountService(
        AccountRepositoryPort accountRepository,
        PasswordHasher passwordHasher,
        TokenUtils tokenIssuer,
        RefreshTokenRepositoryPort refreshTokenRepository,
        @Value("${security.jwt.refresh-expiration-ms}") long refreshTokenExpirationMs
    ){
        return new LoginAccountService(accountRepository, passwordHasher, tokenIssuer, refreshTokenRepository, refreshTokenExpirationMs);
    }

    @Bean
    public RefreshSessionService refreshSessionService(
        AccountRepositoryPort accountRepository,
        PasswordHasher passwordHasher,
        TokenUtils tokenIssuer,
        RefreshTokenRepositoryPort refreshTokenRepository
    ){
        return new RefreshSessionService(tokenIssuer, accountRepository, refreshTokenRepository);
    }
}
