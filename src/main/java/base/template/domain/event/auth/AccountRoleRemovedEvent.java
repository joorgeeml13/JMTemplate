package base.template.domain.event.auth;

import java.time.Instant;
import java.util.UUID;

import base.template.domain.event.DomainEvent;
import base.template.domain.model.auth.Role;

public record AccountRoleRemovedEvent(
    UUID eventId,
    Instant occurredOn,
    UUID accountId,
    Role removedRole
) implements DomainEvent{

    public AccountRoleRemovedEvent(UUID accountId, Role removedRole) {
        this(UUID.randomUUID(), Instant.now(), accountId, removedRole);
    }
}
