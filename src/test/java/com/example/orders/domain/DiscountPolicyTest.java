package com.example.orders.domain;

import com.example.orders.domain.model.*;
import com.example.orders.domain.service.DiscountPolicy;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscountPolicyTest {

    private final DiscountPolicy policy = new DiscountPolicy();

    private Customer customer(CustomerType type) {
        return new Customer(new CustomerId(UUID.randomUUID()), "Teste", type);
    }

    @Test
    void clienteComumEPedidoPequenoNaoTemDesconto() {
        assertEquals(Money.ZERO, policy.calculate(customer(CustomerType.REGULAR), Money.of("500.00")));
    }

    @Test
    void vipTem10PorCento() {
        assertEquals(Money.of("50.00"), policy.calculate(customer(CustomerType.VIP), Money.of("500.00")));
    }

    @Test
    void pedidoGrandeTem5PorCento() {
        assertEquals(Money.of("100.00"), policy.calculate(customer(CustomerType.REGULAR), Money.of("2000.00")));
    }

    @Test
    void vipComPedidoGrandeAcumula15PorCento() {
        assertEquals(Money.of("300.00"), policy.calculate(customer(CustomerType.VIP), Money.of("2000.00")));
    }
}
