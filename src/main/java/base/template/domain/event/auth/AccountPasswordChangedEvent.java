package base.template.domain.event.auth;

import java.time.Instant;
import java.util.UUID;

import base.template.domain.event.DomainEvent;

public record AccountPasswordChangedEvent(
    UUID eventId,
    Instant occurredOn,
    UUID accountId
    ) implements DomainEvent{
    public AccountPasswordChangedEvent(UUID accountId) {
        this(UUID.randomUUID(), Instant.now(), accountId);
    }
}
