package com.example.orders.domain.exception;

import com.example.orders.domain.model.OrderId;

public class OrderNotFoundException extends NotFoundException {
    public OrderNotFoundException(OrderId id) {
        super("Pedido não encontrado: " + id.value());
    }
}
