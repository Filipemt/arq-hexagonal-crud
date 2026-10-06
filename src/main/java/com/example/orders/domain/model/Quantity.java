package com.example.orders.domain.model;

/** Value object: a validação mora no construtor, então uma Quantity inválida não existe. */
public record Quantity(int value) {
    public Quantity {
        if (value <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
    }
}
