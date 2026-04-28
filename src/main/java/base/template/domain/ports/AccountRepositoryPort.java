package base.template.domain.ports;

import java.util.Optional;
import java.util.UUID;

import base.template.domain.model.Account;

public interface AccountRepositoryPort {
    Optional<Account> findByEmail(String email);
    Optional<Account> findById(UUID id);
    Account save(Account account);
    boolean existsByEmail(String email);
}
