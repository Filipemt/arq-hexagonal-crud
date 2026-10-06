package com.example.orders.adapter.out.events;

import com.example.orders.application.port.out.EventPublisher;
import com.example.orders.domain.event.DomainEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adapter outbound (mock): só imprime os eventos no console.
 * Amanhã poderia publicar no Kafka/RabbitMQ sem tocar no núcleo.
 */
@Component
public class LoggingEventPublisher implements EventPublisher {

    @Override
    public void publish(List<DomainEvent> events) {
        events.forEach(e -> System.out.println("[EVENT] " + e));
    }
}
