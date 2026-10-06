package com.example.orders.domain.event;

import com.example.orders.domain.model.Money;
import com.example.orders.domain.model.OrderId;

public record OrderPaid(OrderId orderId, Money amount) implements DomainEvent {}
