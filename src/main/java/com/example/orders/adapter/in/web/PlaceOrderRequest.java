package com.example.orders.adapter.in.web;

import com.example.orders.application.port.in.PlaceOrderCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.UUID;

/**
 * DTO de entrada. A validação de FORMATO (campo nulo, lista vazia) fica aqui, no adapter.
 * As regras de NEGÓCIO ficam no domínio.
 */
public record PlaceOrderRequest(
        @NotNull UUID customerId,
        @NotEmpty @Valid List<ItemRequest> items) {

    public record ItemRequest(@NotNull UUID productId, @Positive int quantity) {}

    PlaceOrderCommand toCommand() {
        return new PlaceOrderCommand(
                customerId,
                items.stream().map(i -> new PlaceOrderCommand.Item(i.productId(), i.quantity())).toList());
    }
}
