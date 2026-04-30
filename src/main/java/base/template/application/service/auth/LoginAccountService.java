package base.template.application.service.auth;

import base.template.application.ports.in.auth.command.LoginCommand;
import base.template.application.ports.in.auth.usecase.LoginAccountUseCase;
import base.template.application.ports.out.AccountRepositoryPort;
import base.template.application.security.AuthTokens;
import base.template.application.security.TokenUtils;
import base.template.domain.exception.auth.InvalidCredentialsException;
import base.template.domain.model.auth.Account;
import base.template.domain.model.auth.Email;
import base.template.domain.model.auth.RawPassword;
import base.template.domain.service.PasswordHasher;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginAccountService implements LoginAccountUseCase{

    private final AccountRepositoryPort accountRepository;
    private final PasswordHasher passwordHasher;
    private final TokenUtils tokenIssuer;

    

    @Override
    public AuthTokens execute(LoginCommand command) {
        Email email = new Email(command.email());
        RawPassword rawPassword = new RawPassword(command.password());

        Account account = accountRepository.findByEmail(email).orElseThrow(InvalidCredentialsException::new);

        if(!passwordHasher.matches(rawPassword, account.getPassword())) throw new InvalidCredentialsException();

        return tokenIssuer.issue(account);
    }
    
}
