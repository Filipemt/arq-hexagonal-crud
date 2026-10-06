package com.example.orders.domain.service;

import com.example.orders.domain.model.Customer;
import com.example.orders.domain.model.CustomerType;
import com.example.orders.domain.model.Money;
import com.example.orders.domain.model.Order;

import java.math.BigDecimal;

/**
 * Domain service: regra que envolve mais de uma entidade (Customer + valor do pedido)
 * e não pertence naturalmente a nenhuma delas. Java puro, sem framework.
 */
public class DiscountPolicy {

    private static final Money BIG_ORDER_THRESHOLD = Money.of("1000.00");

    public Money calculate(Customer customer, Money subtotal) {
        BigDecimal percent = BigDecimal.ZERO;

        if (customer.type() == CustomerType.VIP) {
            percent = percent.add(new BigDecimal("10"));
        }
        if (subtotal.isGreaterThan(BIG_ORDER_THRESHOLD)) {
            percent = percent.add(new BigDecimal("5"));
        }
        percent = percent.min(Order.MAX_DISCOUNT_PERCENT);

        return subtotal.percentage(percent);
    }
}
