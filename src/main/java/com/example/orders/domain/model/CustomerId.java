package com.example.orders.domain.model;

import java.util.Objects;
import java.util.UUID;

/** Value object que tipa o identificador (evita misturar UUIDs de coisas diferentes). */
public record CustomerId(UUID value) {
    public CustomerId {
        Objects.requireNonNull(value, "CustomerId é obrigatório");
    }
}
