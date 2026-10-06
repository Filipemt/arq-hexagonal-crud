package com.example.orders.domain.event;

import com.example.orders.domain.model.OrderId;

public record OrderCancelled(OrderId orderId) implements DomainEvent {}
