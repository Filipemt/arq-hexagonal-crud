package com.example.orders.config;

import com.example.orders.application.port.out.*;
import com.example.orders.application.service.*;
import com.example.orders.domain.service.DiscountPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Composition root: liga as ports outbound (adapters @Component) aos casos de uso.
 * O núcleo (domain + application) continua sem nenhuma anotação do Spring.
 */
@Configuration
public class BeanConfig {

    @Bean
    DiscountPolicy discountPolicy() {
        return new DiscountPolicy();
    }

    @Bean
    PlaceOrderService placeOrderService(OrderRepository orders, CustomerRepository customers,
                                        ProductCatalog catalog, StockService stock,
                                        EventPublisher events, DiscountPolicy discountPolicy) {
        return new PlaceOrderService(orders, customers, catalog, stock, events, discountPolicy);
    }

    @Bean
    PayOrderService payOrderService(OrderRepository orders, PaymentGateway payments, EventPublisher events) {
        return new PayOrderService(orders, payments, events);
    }

    @Bean
    CancelOrderService cancelOrderService(OrderRepository orders, StockService stock,
                                          PaymentGateway payments, EventPublisher events) {
        return new CancelOrderService(orders, stock, payments, events);
    }

    @Bean
    GetOrderService getOrderService(OrderRepository orders) {
        return new GetOrderService(orders);
    }
}
