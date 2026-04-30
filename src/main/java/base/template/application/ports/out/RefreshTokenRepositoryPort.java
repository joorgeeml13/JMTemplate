package base.template.application.ports.out;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import base.template.domain.model.auth.RefreshToken;

public interface RefreshTokenRepositoryPort {
    Optional<RefreshToken> findByToken(String token);
    void save(RefreshToken token);
    void delete(RefreshToken token);
    void revokeAllByAccountId(UUID accountId);
    void deleteExpiredTokens(Instant now);
}
