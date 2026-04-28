package base.template.infrastructure.adapters.output.persistence.mapper;

import org.springframework.stereotype.Component;

import base.template.domain.model.Account;
import base.template.infrastructure.adapters.output.persistence.entity.AccountJpaEntity;

@Component
public class AccountPersistenceMapper {

    public AccountJpaEntity toEntity(Account domainAccount){
        if(domainAccount == null) return null;

        return AccountJpaEntity.builder()
                .id(domainAccount.getId())
                .email(domainAccount.getEmail())
                .password(domainAccount.getPassword())
                .roles(domainAccount.getRoles())
                .createdAt(domainAccount.getCreatedAt())
                .build();
    }

    public Account toDomain(AccountJpaEntity entity){
        if(entity == null) return null;

        return new Account(
            entity.getId(), 
            entity.getEmail(),
            entity.getPassword(),
            entity.getRoles(),
            entity.getCreatedAt()
        );
    }
}
