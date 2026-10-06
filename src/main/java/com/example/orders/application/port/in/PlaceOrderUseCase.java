package com.example.orders.application.port.in;

import com.example.orders.domain.model.Order;

public interface PlaceOrderUseCase {
    Order place(PlaceOrderCommand command);
}
