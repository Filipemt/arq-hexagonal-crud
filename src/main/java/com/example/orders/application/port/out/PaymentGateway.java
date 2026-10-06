package com.example.orders.application.port.out;

import com.example.orders.domain.model.Money;
import com.example.orders.domain.model.OrderId;

public interface PaymentGateway {
    PaymentResult charge(OrderId orderId, Money amount);
    void refund(OrderId orderId, Money amount);
}
