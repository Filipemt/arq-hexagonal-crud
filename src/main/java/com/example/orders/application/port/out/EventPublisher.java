package com.example.orders.application.port.out;

import com.example.orders.domain.event.DomainEvent;
import java.util.List;

public interface EventPublisher {
    void publish(List<DomainEvent> events);
}
