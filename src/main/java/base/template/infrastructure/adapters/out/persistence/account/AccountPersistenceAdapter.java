package base.template.infrastructure.adapters.out.persistence.account;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import base.template.application.ports.out.AccountRepositoryPort;
import base.template.domain.model.auth.Account;
import base.template.domain.model.auth.AccountStatus;
import base.template.domain.model.auth.Email;
import base.template.domain.model.auth.HashedPassword;
import base.template.domain.model.auth.Role;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AccountPersistenceAdapter implements AccountRepositoryPort{

    private final SpringDataAccountRepository repository;

    @Override
    public void save(Account account) {
        AccountEntity entity = AccountEntity.builder()
                .id(account.getId())
                .email(account.getEmail().getValue()) // Extraemos el String del VO
                .password(account.getPassword().getValue()) // Extraemos el String
                .status(account.getStatus().toString())
                .createdAt(account.getCreatedAt())
                .roles(account.getRoles().stream().map(Enum::name).toList())
                .build();
                
        repository.save(entity);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return repository.existsByEmail(email.getValue());
    }

    @Override
    public Optional<Account> findByEmail(Email email) {
        return repository.findByEmail(email.getValue())
                .map(this::mapToDomain);
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return repository.findById(id)
                .map(this::mapToDomain);
    }
    

    private Account mapToDomain(AccountEntity entity) {
        return Account.reconstruct(
                entity.getId(),
                new Email(entity.getEmail()),
                new HashedPassword(entity.getPassword()),
                AccountStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getRoles().stream().map(Role::valueOf).toList()
        );
    }
}
