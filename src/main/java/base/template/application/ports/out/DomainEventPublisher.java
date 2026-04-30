package base.template.application.ports.out;

import base.template.domain.event.DomainEvent;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
