package base.template.domain.model.auth;

import java.time.Instant;
import java.util.UUID;

import base.template.domain.exception.auth.InvalidAccountDataException;
import lombok.Getter;

public class RefreshToken {
    
    @Getter private final UUID id;
    @Getter private final String tokenValue;
    @Getter private final Account account;
    @Getter private final Instant expiryDate;
    @Getter private boolean revoked;
    @Getter private String replacedByToken;

    private RefreshToken(UUID id, String tokenValue, Account account, Instant expiryDate, boolean revoked, String replacedByToken) {
        this.id = id;
        this.tokenValue = tokenValue;
        this.account = account;
        this.expiryDate = expiryDate;
        this.revoked = revoked;
        this.replacedByToken = replacedByToken;
    }

    public static RefreshToken create(String tokenValue, Account account, long durationMs) {
        if (tokenValue == null || tokenValue.isBlank()) throw new InvalidAccountDataException("El token no puede estar vacío");
        if (account == null) throw new InvalidAccountDataException("La cuenta no puede ser nula");
        
        return new RefreshToken(
            UUID.randomUUID(), 
            tokenValue, 
            account, 
            Instant.now().plusMillis(durationMs), 
            false, 
            null
        );
    }

    public static RefreshToken reconstruct(UUID id, String tokenValue, Account account, Instant expiryDate, boolean revoked, String replacedByToken) {
        return new RefreshToken(id, tokenValue, account, expiryDate, revoked, replacedByToken);
    }

    public void revoke(String replacedByToken) {
        if (this.revoked) throw new InvalidAccountDataException("error.token.revocado");
        this.revoked = true;
        this.replacedByToken = replacedByToken;
    }
}