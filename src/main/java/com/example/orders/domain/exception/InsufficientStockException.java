package com.example.orders.domain.exception;

import com.example.orders.domain.model.ProductId;

public class InsufficientStockException extends DomainException {
    public InsufficientStockException(ProductId id, int requested, int available) {
        super("Estoque insuficiente para o produto " + id.value()
                + " (solicitado: " + requested + ", disponível: " + available + ")");
    }
}
