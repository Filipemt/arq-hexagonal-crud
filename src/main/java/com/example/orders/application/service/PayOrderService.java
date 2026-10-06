package com.example.orders.application.service;

import com.example.orders.application.port.in.PayOrderUseCase;
import com.example.orders.application.port.out.EventPublisher;
import com.example.orders.application.port.out.OrderRepository;
import com.example.orders.application.port.out.PaymentGateway;
import com.example.orders.application.port.out.PaymentResult;
import com.example.orders.domain.exception.OrderNotFoundException;
import com.example.orders.domain.exception.PaymentFailedException;
import com.example.orders.domain.model.Order;
import com.example.orders.domain.model.OrderId;

import java.util.UUID;

public class PayOrderService implements PayOrderUseCase {

    private final OrderRepository orders;
    private final PaymentGateway payments;
    private final EventPublisher events;

    public PayOrderService(OrderRepository orders, PaymentGateway payments, EventPublisher events) {
        this.orders = orders;
        this.payments = payments;
        this.events = events;
    }

    @Override
    public Order pay(UUID orderId) {
        OrderId id = new OrderId(orderId);
        Order order = orders.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        order.assertPayable();                                     // domínio: pode pagar?
        PaymentResult result = payments.charge(id, order.total()); // port out: cobra
        if (!result.approved()) {
            throw new PaymentFailedException(result.reason());
        }

        order.pay();                                               // domínio: muda o estado
        orders.save(order);
        events.publish(order.pullEvents());
        return order;
    }
}
