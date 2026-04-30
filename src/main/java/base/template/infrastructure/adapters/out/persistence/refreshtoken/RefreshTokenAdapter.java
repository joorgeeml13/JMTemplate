package base.template.infrastructure.adapters.out.persistence.refreshtoken;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import base.template.application.ports.out.RefreshTokenRepositoryPort;
import base.template.domain.model.auth.Account;
import base.template.domain.model.auth.AccountStatus;
import base.template.domain.model.auth.Email;
import base.template.domain.model.auth.HashedPassword;
import base.template.domain.model.auth.RefreshToken;
import base.template.infrastructure.adapters.out.persistence.account.AccountEntity;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RefreshTokenAdapter implements RefreshTokenRepositoryPort{

    private final SpringDataRefreshTokenRepository refreshTokenRepository;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(this::toDomain);
    }

    @Override
    public void save(RefreshToken token) {
        refreshTokenRepository.save(toEntity(token));
    }

    @Override
    public void delete(RefreshToken token) {
        refreshTokenRepository.deleteById(token.getId());
    }

    @Override
    public void revokeAllByAccountId(UUID accountId) {
        refreshTokenRepository.revokeAllTokensByAccountId(accountId);
    }

    @Override
    @Transactional
    public void deleteExpiredTokens(Instant now) {
        refreshTokenRepository.deleteByExpiryDateBefore(now);
    }
    
    private RefreshToken toDomain(RefreshTokenEntity entity) {
        Account domainAccount = Account.reconstruct(
            entity.getAccount().getId(),
            new Email(entity.getAccount().getEmail()),
            new HashedPassword(entity.getAccount().getPassword()),
            AccountStatus.valueOf(entity.getAccount().getStatus()),
            entity.getAccount().getCreatedAt(),
            new ArrayList<>()
        );

        return RefreshToken.reconstruct(
            entity.getId(),
            entity.getToken(),
            domainAccount,
            entity.getExpiryDate(),
            entity.isRevoked(),
            entity.getReplacedByToken()
        );
    }
    private RefreshTokenEntity toEntity(RefreshToken domain) {
        return RefreshTokenEntity.builder()
            .id(domain.getId())
            .token(domain.getTokenValue())
            .account(AccountEntity.builder().id(domain.getAccount().getId()).build())
            .expiryDate(domain.getExpiryDate())
            .revoked(domain.isRevoked())
            .replacedByToken(domain.getReplacedByToken())
            .build();
    }
}
