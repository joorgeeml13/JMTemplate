package base.template.infrastructure.adapters.out.persistence.refreshtoken;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface SpringDataRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID>{
    Optional<RefreshTokenEntity> findByToken(String token);

    @Modifying
    @Query("UPDATE RefreshTokenEntity r SET r.revoked = true WHERE r.account.id = :accountId")
    void revokeAllTokensByAccountId(@Param("accountId") UUID accountId);
    @Modifying
    @Query("DELETE FROM RefreshTokenEntity r WHERE r.expiryDate < :now")
    void deleteByExpiryDateBefore(@Param("now") Instant now);
}
