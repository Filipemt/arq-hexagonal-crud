package com.example.orders.domain.event;

import com.example.orders.domain.model.CustomerId;
import com.example.orders.domain.model.Money;
import com.example.orders.domain.model.OrderId;

public record OrderPlaced(OrderId orderId, CustomerId customerId, Money total) implements DomainEvent {}
