package base.template.infrastructure.adapters.out.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import base.template.application.ports.out.DomainEventPublisher;
import base.template.domain.event.DomainEvent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpringEventPublisherAdapter implements DomainEventPublisher{
    
    private final ApplicationEventPublisher springEventPublisher;

    @Override
    public void publish(DomainEvent event) {
        springEventPublisher.publishEvent(event);
    }
    
}
