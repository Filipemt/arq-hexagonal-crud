package com.example.orders.application.service;

import com.example.orders.application.port.in.GetOrderUseCase;
import com.example.orders.application.port.out.OrderRepository;
import com.example.orders.domain.exception.OrderNotFoundException;
import com.example.orders.domain.model.Order;
import com.example.orders.domain.model.OrderId;

import java.util.UUID;

public class GetOrderService implements GetOrderUseCase {

    private final OrderRepository orders;

    public GetOrderService(OrderRepository orders) {
        this.orders = orders;
    }

    @Override
    public Order get(UUID orderId) {
        OrderId id = new OrderId(orderId);
        return orders.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }
}
