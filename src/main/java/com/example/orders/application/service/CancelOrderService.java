package com.example.orders.application.service;

import com.example.orders.application.port.in.CancelOrderUseCase;
import com.example.orders.application.port.out.EventPublisher;
import com.example.orders.application.port.out.OrderRepository;
import com.example.orders.application.port.out.PaymentGateway;
import com.example.orders.application.port.out.StockService;
import com.example.orders.domain.exception.OrderNotFoundException;
import com.example.orders.domain.model.Order;
import com.example.orders.domain.model.OrderId;

import java.util.UUID;

/** Cancelar: libera estoque e, se o pedido já estava pago, estorna o valor. */
public class CancelOrderService implements CancelOrderUseCase {

    private final OrderRepository orders;
    private final StockService stock;
    private final PaymentGateway payments;
    private final EventPublisher events;

    public CancelOrderService(OrderRepository orders, StockService stock,
                              PaymentGateway payments, EventPublisher events) {
        this.orders = orders;
        this.stock = stock;
        this.payments = payments;
        this.events = events;
    }

    @Override
    public Order cancel(UUID orderId) {
        OrderId id = new OrderId(orderId);
        Order order = orders.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        boolean wasPaid = order.isPaid();
        order.cancel();                       // domínio valida (ex.: já cancelado) e muda estado

        stock.release(order.getItems());
        if (wasPaid) {
            payments.refund(id, order.total());
        }

        orders.save(order);
        events.publish(order.pullEvents());
        return order;
    }
}
