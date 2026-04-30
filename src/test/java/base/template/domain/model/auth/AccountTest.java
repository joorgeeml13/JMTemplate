package base.template.domain.model.auth;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import base.template.domain.DomainConstants;
import base.template.domain.event.DomainEvent;
import base.template.domain.event.auth.AccountPasswordChangedEvent;
import base.template.domain.event.auth.AccountRoleAddedEvent;
import base.template.domain.event.auth.AccountRoleRemovedEvent;
import base.template.domain.exception.auth.InvalidAccountDataException;

class AccountTest {

    @Test
    void testCreateAccount() {
        // Given
        Email email = new Email("test@example.com");
        HashedPassword password = new HashedPassword("hashedPassword123");

        // When
        Account account = Account.create(email, password);

        // Then
        assertNotNull(account.getId());
        assertEquals(email, account.getEmail());
        assertEquals(password, account.getPassword());
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
        assertNotNull(account.getCreatedAt());
        assertTrue(account.getCreatedAt().isBefore(Instant.now().plusSeconds(1)));
        assertEquals(List.of(DomainConstants.DEFAULT_ROLE), account.getRoles());
    }

    @Test
    void testReconstructAccount() {
        // Given
        UUID id = UUID.randomUUID();
        Email email = new Email("test@example.com");
        HashedPassword password = new HashedPassword("hashedPassword123");
        AccountStatus status = AccountStatus.INACTIVE;
        Instant createdAt = Instant.now().minusSeconds(3600);
        List<Role> roles = new java.util.ArrayList<>(List.of(Role.USER, Role.ADMIN));

        // When
        Account account = Account.reconstruct(id, email, password, status, createdAt, roles);

        // Then
        assertEquals(id, account.getId());
        assertEquals(email, account.getEmail());
        assertEquals(password, account.getPassword());
        assertEquals(status, account.getStatus());
        assertEquals(createdAt, account.getCreatedAt());
        assertEquals(roles, account.getRoles());
    }

    @Test
    void testChangePassword() {
        // Given
        Account account = createTestAccount();
        HashedPassword newPassword = new HashedPassword("newHashedPassword456");

        // When
        account.changePassword(newPassword);

        // Then
        assertEquals(newPassword, account.getPassword());
        List<DomainEvent> events = account.pullDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(AccountPasswordChangedEvent.class, events.get(0));
    }

    @Test
    void testChangePasswordNullThrowsException() {
        // Given
        Account account = createTestAccount();

        // When & Then
        assertThrows(InvalidAccountDataException.class, () -> account.changePassword(null));
    }

    @Test
    void testChangePasswordSameAsCurrentThrowsException() {
        // Given
        Account account = createTestAccount();
        HashedPassword currentPassword = account.getPassword();

        // When & Then
        assertThrows(InvalidAccountDataException.class, () -> account.changePassword(currentPassword));
    }

    @Test
    void testChangeStatus() {
        // Given
        Account account = createTestAccount();

        // When
        account.changeStatus(AccountStatus.SUSPENDED);

        // Then
        assertEquals(AccountStatus.SUSPENDED, account.getStatus());
    }

    @Test
    void testChangeStatusNullThrowsException() {
        // Given
        Account account = createTestAccount();

        // When & Then
        assertThrows(InvalidAccountDataException.class, () -> account.changeStatus(null));
    }

    @Test
    void testChangeStatusSameAsCurrentThrowsException() {
        // Given
        Account account = createTestAccount();

        // When & Then
        assertThrows(InvalidAccountDataException.class, () -> account.changeStatus(account.getStatus()));
    }

    @Test
    void testAddRole() {
        // Given
        Account account = createTestAccount();

        // When
        account.addRole(Role.ADMIN);

        // Then
        assertTrue(account.getRoles().contains(Role.ADMIN));
        List<DomainEvent> events = account.pullDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(AccountRoleAddedEvent.class, events.get(0));
    }

    @Test
    void testAddRoleNullThrowsException() {
        // Given
        Account account = createTestAccount();

        // When & Then
        assertThrows(InvalidAccountDataException.class, () -> account.addRole(null));
    }

    @Test
    void testAddRoleDuplicateThrowsException() {
        // Given
        Account account = createTestAccount();

        // When & Then
        assertThrows(InvalidAccountDataException.class, () -> account.addRole(Role.USER));
    }

    @Test
    void testRemoveRole() {
        // Given
        Account account = createTestAccount();
        account.addRole(Role.ADMIN);

        // When
        account.removeRole(Role.ADMIN);

        // Then
        assertFalse(account.getRoles().contains(Role.ADMIN));
        List<DomainEvent> events = account.pullDomainEvents();
        assertEquals(2, events.size()); // add + remove events
        assertInstanceOf(AccountRoleRemovedEvent.class, events.get(1));
    }

    @Test
    void testRemoveRoleNotFoundThrowsException() {
        // Given
        Account account = createTestAccount();

        // When & Then
        assertThrows(InvalidAccountDataException.class, () -> account.removeRole(Role.ADMIN));
    }

    @Test
    void testRemoveLastRoleThrowsException() {
        // Given
        Account account = createTestAccount();

        // When & Then
        assertThrows(InvalidAccountDataException.class, () -> account.removeRole(Role.USER));
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        UUID id = UUID.randomUUID();
        Account account1 = Account.reconstruct(id, new Email("test@example.com"),
                new HashedPassword("password"), AccountStatus.ACTIVE, Instant.now(), new java.util.ArrayList<>(List.of(Role.USER)));
        Account account2 = Account.reconstruct(id, new Email("other@example.com"),
                new HashedPassword("other"), AccountStatus.INACTIVE, Instant.now(), new java.util.ArrayList<>(List.of(Role.ADMIN)));
        Account account3 = Account.reconstruct(UUID.randomUUID(), new Email("test@example.com"),
                new HashedPassword("password"), AccountStatus.ACTIVE, Instant.now(), new java.util.ArrayList<>(List.of(Role.USER)));

        // Then
        assertEquals(account1, account2); // Same ID
        assertNotEquals(account1, account3); // Different ID
        assertEquals(account1.hashCode(), account2.hashCode()); // Same hash for same ID
        assertNotEquals(account1.hashCode(), account3.hashCode()); // Different hash for different ID
    }

    @Test
    void testPullDomainEvents() {
        // Given
        Account account = createTestAccount();
        account.changePassword(new HashedPassword("newPassword"));
        account.addRole(Role.ADMIN);

        // When
        List<DomainEvent> events = account.pullDomainEvents();

        // Then
        assertEquals(2, events.size());
        assertTrue(events.stream().anyMatch(e -> e instanceof AccountPasswordChangedEvent));
        assertTrue(events.stream().anyMatch(e -> e instanceof AccountRoleAddedEvent));

        // Events should be cleared after pulling
        assertEquals(0, account.pullDomainEvents().size());
    }

    private Account createTestAccount() {
        return Account.reconstruct(
                UUID.randomUUID(),
                new Email("test@example.com"),
                new HashedPassword("hashedPassword123"),
                AccountStatus.ACTIVE,
                Instant.now(),
                new java.util.ArrayList<>(List.of(Role.USER))
        );
    }
}