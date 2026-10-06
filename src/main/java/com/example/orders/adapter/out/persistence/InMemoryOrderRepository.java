package com.example.orders.adapter.out.persistence;

import com.example.orders.application.port.out.OrderRepository;
import com.example.orders.domain.model.Order;
import com.example.orders.domain.model.OrderId;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adapter outbound (mock): guarda o próprio objeto de domínio em um Map.
 * Como não há banco, não existe entidade JPA nem mapeamento. Com JPA,
 * este adapter converteria Order <-> OrderJpaEntity, como no projeto anterior.
 */
@Component
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<OrderId, Order> store = new ConcurrentHashMap<>();

    @Override
    public Order save(Order order) {
        store.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return Optional.ofNullable(store.get(id));
    }
}
