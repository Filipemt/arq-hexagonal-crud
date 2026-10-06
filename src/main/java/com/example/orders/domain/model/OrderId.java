package com.example.orders.domain.model;

import java.util.Objects;
import java.util.UUID;

public record OrderId(UUID value) {
    public OrderId {
        Objects.requireNonNull(value, "OrderId é obrigatório");
    }

    public static OrderId newId() {
        return new OrderId(UUID.randomUUID());
    }
}
