package base.template.infrastructure.adapters.out.persistence.refreshtoken;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

import base.template.infrastructure.adapters.out.persistence.account.AccountEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenEntity {
    
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    private boolean revoked;

    private String replacedByToken;
}
