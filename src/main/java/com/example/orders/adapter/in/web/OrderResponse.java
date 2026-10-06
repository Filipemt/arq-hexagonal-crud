package com.example.orders.adapter.in.web;

import com.example.orders.domain.model.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** DTO de saída: o domínio nunca vai direto para o JSON. */
public record OrderResponse(
        UUID id,
        UUID customerId,
        String status,
        List<ItemResponse> items,
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal total) {

    public record ItemResponse(UUID productId, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {}

    static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId().value(),
                order.getCustomerId().value(),
                order.getStatus().name(),
                order.getItems().stream()
                        .map(i -> new ItemResponse(
                                i.productId().value(),
                                i.quantity().value(),
                                i.unitPrice().amount(),
                                i.subtotal().amount()))
                        .toList(),
                order.subtotal().amount(),
                order.getDiscount().amount(),
                order.total().amount());
    }
}
