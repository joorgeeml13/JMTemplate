package base.template.application.ports.out;

import java.util.Optional;
import java.util.UUID;

import base.template.domain.model.auth.Account;
import base.template.domain.model.auth.Email;

public interface AccountRepositoryPort {
    void save(Account account);
    boolean existsByEmail(Email email);
    Optional<Account> findByEmail(Email email);
    Optional<Account> findById(UUID id);
}
