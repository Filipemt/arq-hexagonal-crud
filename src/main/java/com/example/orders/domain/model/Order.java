package com.example.orders.domain.model;

import com.example.orders.domain.event.DomainEvent;
import com.example.orders.domain.event.OrderCancelled;
import com.example.orders.domain.event.OrderPaid;
import com.example.orders.domain.event.OrderPlaced;
import com.example.orders.domain.exception.InvalidOrderStateException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate root. Aqui ficam as invariantes e as transições de estado.
 * Repare: O estado só muda por métodos de negócio.
 */
public class Order {

    public static final BigDecimal MAX_DISCOUNT_PERCENT = new BigDecimal("30");

    private final OrderId id;
    private final CustomerId customerId;
    private final List<OrderItem> items;
    private final Money discount;
    private OrderStatus status;
    private final List<DomainEvent> events = new ArrayList<>();

    private Order(OrderId id, CustomerId customerId, List<OrderItem> items, Money discount) {
        this.id = id;
        this.customerId = customerId;
        this.items = List.copyOf(items);
        this.discount = discount;
        this.status = OrderStatus.CREATED;
    }

    public static Order place(CustomerId customerId, List<OrderItem> items, Money discount) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Pedido deve ter ao menos um item");
        }
        if (items.size() > 10) {
            throw new IllegalArgumentException("Pedido não pode ter mais de 10 itens");
        }

        Order order = new Order(OrderId.newId(), customerId, items, discount);
        order.validateDiscount();
        order.events.add(new OrderPlaced(order.id, customerId, order.total()));
        return order;
    }

    // ---------- regras de negócio ----------

    /** Invariante: o desconto nunca passa de 30% do subtotal. */
    private void validateDiscount() {
        Money maxAllowed = subtotal().percentage(MAX_DISCOUNT_PERCENT);
        if (discount.isGreaterThan(maxAllowed)) {
            throw new IllegalArgumentException("Desconto acima do máximo permitido de "
                    + MAX_DISCOUNT_PERCENT + "% do subtotal");
        }
    }

    /** Usado pelo caso de uso ANTES de cobrar, para não cobrar um pedido que não pode ser pago. */
    public void assertPayable() {
        if (status != OrderStatus.CREATED) {
            throw new InvalidOrderStateException(
                    "Só é possível pagar pedido CREATED. Status atual: " + status);
        }
    }

    public void pay() {
        assertPayable();
        this.status = OrderStatus.PAID;
        events.add(new OrderPaid(id, total()));
    }

    public void cancel() {
        if (status == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("Pedido já está cancelado");
        }
        this.status = OrderStatus.CANCELLED;
        events.add(new OrderCancelled(id));
    }

    // ---------- consultas ----------

    public Money subtotal() {
        return items.stream().map(OrderItem::subtotal).reduce(Money.ZERO, Money::plus);
    }

    public Money total() {
        return subtotal().minus(discount);
    }

    public boolean isPaid() {
        return status == OrderStatus.PAID;
    }

    /** Entrega os eventos acumulados e limpa a lista. Quem publica é o caso de uso. */
    public List<DomainEvent> pullEvents() {
        List<DomainEvent> copy = List.copyOf(events);
        events.clear();
        return copy;
    }

    public OrderId getId() { return id; }
    public CustomerId getCustomerId() { return customerId; }
    public List<OrderItem> getItems() { return items; }
    public Money getDiscount() { return discount; }
    public OrderStatus getStatus() { return status; }
}
