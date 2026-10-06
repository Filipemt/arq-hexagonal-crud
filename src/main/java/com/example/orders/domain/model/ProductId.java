package com.example.orders.domain.model;

import java.util.Objects;
import java.util.UUID;

/** Value object que tipa o identificador (evita misturar UUIDs de coisas diferentes). */
public record ProductId(UUID value) {
    public ProductId {
        Objects.requireNonNull(value, "ProductId é obrigatório");
    }
}
