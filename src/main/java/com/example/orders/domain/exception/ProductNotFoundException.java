package com.example.orders.domain.exception;

import com.example.orders.domain.model.ProductId;

public class ProductNotFoundException extends NotFoundException {
    public ProductNotFoundException(ProductId id) {
        super("Produto não encontrado: " + id.value());
    }
}
