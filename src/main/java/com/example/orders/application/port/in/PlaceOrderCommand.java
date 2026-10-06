package com.example.orders.application.port.in;

import java.util.List;
import java.util.UUID;

public record PlaceOrderCommand(UUID customerId, List<Item> items) {
    public record Item(UUID productId, int quantity) {}
}
