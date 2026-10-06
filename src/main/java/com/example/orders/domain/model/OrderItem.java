package com.example.orders.domain.model;

public record OrderItem(ProductId productId, Quantity quantity, Money unitPrice) {

    public Money subtotal() {
        return unitPrice.times(quantity.value());
    }
}
