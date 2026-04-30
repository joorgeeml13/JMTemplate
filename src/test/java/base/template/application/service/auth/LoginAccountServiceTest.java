package base.template.application.service.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import base.template.application.ports.in.auth.command.LoginCommand;
import base.template.application.ports.out.AccountRepositoryPort;
import base.template.application.ports.out.RefreshTokenRepositoryPort;
import base.template.application.security.AuthTokens;
import base.template.application.security.TokenUtils;
import base.template.domain.exception.auth.InvalidCredentialsException;
import base.template.domain.model.auth.Account;
import base.template.domain.model.auth.AccountStatus;
import base.template.domain.model.auth.Email;
import base.template.domain.model.auth.HashedPassword;
import base.template.domain.model.auth.RefreshToken;
import base.template.domain.model.auth.Role;
import base.template.domain.model.auth.RawPassword;
import base.template.domain.service.PasswordHasher;

class LoginAccountServiceTest {

    @Mock
    private AccountRepositoryPort accountRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private TokenUtils tokenIssuer;

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;

    private LoginAccountService loginAccountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loginAccountService = new LoginAccountService(accountRepository, passwordHasher, tokenIssuer, refreshTokenRepository, 604800000L);
    }

    @Test
    void testLoginRevokesPreviousTokensAndPersistsNewRefreshToken() {
        // Given
        UUID accountId = UUID.randomUUID();
        Account account = Account.reconstruct(
            accountId,
            new Email("test@example.com"),
            new HashedPassword("hashed-password"),
            AccountStatus.ACTIVE,
            Instant.now(),
            new java.util.ArrayList<>(java.util.List.of(Role.USER))
        );
        when(accountRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(account));
        when(passwordHasher.matches(any(RawPassword.class), eq(account.getPassword()))).thenReturn(true);
        when(tokenIssuer.issue(account)).thenReturn(new AuthTokens("access-token", "refresh-token"));

        // When
        AuthTokens tokens = loginAccountService.execute(new LoginCommand("test@example.com", "password123"));

        // Then
        assertNotNull(tokens);
        assertEquals("access-token", tokens.accessToken());
        assertEquals("refresh-token", tokens.refreshToken());

        verify(refreshTokenRepository).deleteExpiredTokens(any(Instant.class));
        verify(refreshTokenRepository).revokeAllByAccountId(eq(accountId));

        ArgumentCaptor<RefreshToken> refreshTokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(refreshTokenCaptor.capture());

        RefreshToken persisted = refreshTokenCaptor.getValue();
        assertNotNull(persisted.getId());
        assertEquals("refresh-token", persisted.getTokenValue());
        assertEquals(accountId, persisted.getAccount().getId());
        assertFalse(persisted.isRevoked());
        assertNull(persisted.getReplacedByToken());
        assertTrue(persisted.getExpiryDate().isAfter(Instant.now()));
    }

    @Test
    void testLoginThrowsWhenCredentialsInvalid() {
        when(accountRepository.findByEmail(new Email("test@example.com"))).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
            () -> loginAccountService.execute(new LoginCommand("test@example.com", "password123")));
    }
}
