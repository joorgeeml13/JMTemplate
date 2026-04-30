package base.template.application.service.auth;

import base.template.application.ports.in.auth.command.RegisterCommand;
import base.template.application.ports.in.auth.usecase.RegisterAccountUseCase;
import base.template.application.ports.out.AccountRepositoryPort;
import base.template.application.ports.out.DomainEventPublisher;
import base.template.domain.exception.auth.InvalidAccountDataException;
import base.template.domain.model.auth.Account;
import base.template.domain.model.auth.Email;
import base.template.domain.model.auth.HashedPassword;
import base.template.domain.model.auth.RawPassword;
import base.template.domain.service.PasswordHasher;

public class RegisterAccountService implements RegisterAccountUseCase{

    private final AccountRepositoryPort accountRepository;
    private final PasswordHasher passwordHasher;
    private final DomainEventPublisher eventPublisher;

    public RegisterAccountService(AccountRepositoryPort accountRepository, 
                                  PasswordHasher passwordHasher, 
                                  DomainEventPublisher eventPublisher) {
        this.accountRepository = accountRepository;
        this.passwordHasher = passwordHasher;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void execute(RegisterCommand command) {
        Email email = new Email(command.email());
        RawPassword rawPassword = new RawPassword(command.password());

        if (accountRepository.existsByEmail(email)) throw new InvalidAccountDataException("error.account.email.already_exists");

        HashedPassword hashedPassword = passwordHasher.hash(rawPassword);

        Account newAccount = Account.create(email, hashedPassword);

        accountRepository.save(newAccount);

        newAccount.pullDomainEvents().forEach(eventPublisher::publish);
    }
}