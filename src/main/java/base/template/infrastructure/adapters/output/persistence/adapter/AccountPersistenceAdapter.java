package base.template.infrastructure.adapters.output.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import base.template.domain.model.Account;
import base.template.domain.ports.AccountRepositoryPort;
import base.template.infrastructure.adapters.output.persistence.entity.AccountJpaEntity;
import base.template.infrastructure.adapters.output.persistence.mapper.AccountPersistenceMapper;
import base.template.infrastructure.adapters.output.persistence.repository.SpringDataAccountRepository;

@Component
public class AccountPersistenceAdapter implements AccountRepositoryPort{

    private final SpringDataAccountRepository springRepository;
    private final AccountPersistenceMapper mapper;

    public AccountPersistenceAdapter(SpringDataAccountRepository springRepository, AccountPersistenceMapper mapper) {
        this.springRepository = springRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        return springRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Account save(Account account) {
       AccountJpaEntity entity = mapper.toEntity(account);
       AccountJpaEntity savedEntity = springRepository.save(entity);
       return mapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springRepository.existsByEmail(email);
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return springRepository.findById(id).map(mapper::toDomain);
    }

}
