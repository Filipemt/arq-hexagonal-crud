package com.example.orders.application.service;

import com.example.orders.application.port.in.PlaceOrderCommand;
import com.example.orders.application.port.in.PlaceOrderUseCase;
import com.example.orders.application.port.out.*;
import com.example.orders.domain.exception.CustomerNotFoundException;
import com.example.orders.domain.exception.ProductNotFoundException;
import com.example.orders.domain.model.*;
import com.example.orders.domain.service.DiscountPolicy;

import java.util.List;

/**
 * Caso de uso: fazer um pedido.
 * O service ORQUESTRA: busca dados (ports out), delega a regra ao domínio,
 * reserva estoque, persiste e publica eventos. Quase nenhum "if" de negócio aqui.
 */
public class PlaceOrderService implements PlaceOrderUseCase {

    private final OrderRepository orders;
    private final CustomerRepository customers;
    private final ProductCatalog catalog;
    private final StockService stock;
    private final EventPublisher events;
    private final DiscountPolicy discountPolicy;

    public PlaceOrderService(OrderRepository orders, CustomerRepository customers,
                             ProductCatalog catalog, StockService stock,
                             EventPublisher events, DiscountPolicy discountPolicy) {
        this.orders = orders;
        this.customers = customers;
        this.catalog = catalog;
        this.stock = stock;
        this.events = events;
        this.discountPolicy = discountPolicy;
    }

    @Override
    public Order place(PlaceOrderCommand command) {
        if (command.items() == null || command.items().isEmpty()) {
            throw new IllegalArgumentException("Pedido deve ter ao menos um item");
        }

        // 1. Regra que depende de dado externo: o cliente precisa existir
        CustomerId customerId = new CustomerId(command.customerId());
        Customer customer = customers.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        // 2. Monta os itens com preço vindo do catálogo
        List<OrderItem> items = command.items().stream().map(i -> {
            ProductId productId = new ProductId(i.productId());
            Money price = catalog.findPrice(productId)
                    .orElseThrow(() -> new ProductNotFoundException(productId));
            return new OrderItem(productId, new Quantity(i.quantity()), price);
        }).toList();

        // 3. Regra de desconto: delegada ao domain service
        Money subtotal = items.stream().map(OrderItem::subtotal).reduce(Money.ZERO, Money::plus);
        Money discount = discountPolicy.calculate(customer, subtotal);

        // 4. O aggregate valida as próprias invariantes
        Order order = Order.place(customerId, items, discount);

        // 5. Efeitos externos e persistência
        stock.reserve(items);
        orders.save(order);
        events.publish(order.pullEvents());

        return order;
    }
}
