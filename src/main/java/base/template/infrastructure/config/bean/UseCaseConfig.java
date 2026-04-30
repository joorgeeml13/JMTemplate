package base.template.infrastructure.config.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import base.template.application.ports.out.AccountRepositoryPort;
import base.template.application.ports.out.DomainEventPublisher;
import base.template.application.security.TokenUtils;
import base.template.application.service.auth.LoginAccountService;
import base.template.application.service.auth.RefreshSessionService;
import base.template.application.service.auth.RegisterAccountService;
import base.template.domain.service.PasswordHasher;

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
        TokenUtils tokenIssuer
    ){return new LoginAccountService(accountRepository, passwordHasher, tokenIssuer);}

    @Bean
    public RefreshSessionService refreshSessionService(
        AccountRepositoryPort accountRepository,
        PasswordHasher passwordHasher,
        TokenUtils tokenIssuer
    ){return new RefreshSessionService(tokenIssuer, accountRepository);}
}
