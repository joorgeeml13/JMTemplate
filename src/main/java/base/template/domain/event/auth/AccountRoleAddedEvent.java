package base.template.domain.event.auth;

import java.time.Instant;
import java.util.UUID;

import base.template.domain.event.DomainEvent;
import base.template.domain.model.auth.Role;

public record AccountRoleAddedEvent(
    UUID eventId,
    Instant occurredOn,
    UUID accountId,
    Role addedRole
) implements DomainEvent{
    public AccountRoleAddedEvent(UUID accountId, Role addedRole) {
        this(UUID.randomUUID(), Instant.now(), accountId, addedRole);
    }
}
