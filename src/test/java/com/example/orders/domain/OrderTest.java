package com.example.orders.domain;

import com.example.orders.domain.event.OrderPlaced;
import com.example.orders.domain.exception.InvalidOrderStateException;
import com.example.orders.domain.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/** Teste de domínio puro: sem Spring, sem mocks, sem banco. */
class OrderTest {

    private static final CustomerId CUSTOMER = new CustomerId(UUID.randomUUID());

    private OrderItem item(String price, int qty) {
        return new OrderItem(new ProductId(UUID.randomUUID()), new Quantity(qty), Money.of(price));
    }

    @Test
    void calculaSubtotalETotalComDesconto() {
        Order order = Order.place(CUSTOMER, List.of(item("100.00", 2), item("50.00", 1)), Money.of("25.00"));

        assertEquals(Money.of("250.00"), order.subtotal());
        assertEquals(Money.of("225.00"), order.total());
        assertEquals(OrderStatus.CREATED, order.getStatus());
    }

    @Test
    void rejeitaDescontoAcimaDe30PorCento() {
        assertThrows(IllegalArgumentException.class,
                () -> Order.place(CUSTOMER, List.of(item("100.00", 1)), Money.of("30.01")));
    }

    @Test
    void rejeitaPedidoSemItens() {
        assertThrows(IllegalArgumentException.class,
                () -> Order.place(CUSTOMER, List.of(), Money.ZERO));
    }

    @Test
    void rejeitaQuantidadeInvalida() {
        assertThrows(IllegalArgumentException.class, () -> new Quantity(0));
    }

    @Test
    void naoPodePagarDuasVezes() {
        Order order = Order.place(CUSTOMER, List.of(item("10.00", 1)), Money.ZERO);
        order.pay();

        assertTrue(order.isPaid());
        assertThrows(InvalidOrderStateException.class, order::pay);
    }

    @Test
    void naoPodeCancelarDuasVezes() {
        Order order = Order.place(CUSTOMER, List.of(item("10.00", 1)), Money.ZERO);
        order.cancel();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertThrows(InvalidOrderStateException.class, order::cancel);
    }

    @Test
    void naoPodePagarPedidoCancelado() {
        Order order = Order.place(CUSTOMER, List.of(item("10.00", 1)), Money.ZERO);
        order.cancel();

        assertThrows(InvalidOrderStateException.class, order::pay);
    }

    @Test
    void registraEventosDeDominioEOsEntregaUmaVez() {
        Order order = Order.place(CUSTOMER, List.of(item("10.00", 1)), Money.ZERO);

        var events = order.pullEvents();
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof OrderPlaced);
        assertTrue(order.pullEvents().isEmpty());
    }
}
