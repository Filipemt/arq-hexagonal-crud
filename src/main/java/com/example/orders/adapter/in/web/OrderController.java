package com.example.orders.adapter.in.web;

import com.example.orders.application.port.in.CancelOrderUseCase;
import com.example.orders.application.port.in.GetOrderUseCase;
import com.example.orders.application.port.in.PayOrderUseCase;
import com.example.orders.application.port.in.PlaceOrderUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/** Adapter inbound: HTTP -> casos de uso. Só conhece as interfaces (ports). */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final PlaceOrderUseCase placeOrder;
    private final PayOrderUseCase payOrder;
    private final CancelOrderUseCase cancelOrder;
    private final GetOrderUseCase getOrder;

    public OrderController(PlaceOrderUseCase placeOrder, PayOrderUseCase payOrder,
                           CancelOrderUseCase cancelOrder, GetOrderUseCase getOrder) {
        this.placeOrder = placeOrder;
        this.payOrder = payOrder;
        this.cancelOrder = cancelOrder;
        this.getOrder = getOrder;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse place(@Valid @RequestBody PlaceOrderRequest request) {
        return OrderResponse.from(placeOrder.place(request.toCommand()));
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable UUID id) {
        return OrderResponse.from(getOrder.get(id));
    }

    @PostMapping("/{id}/pay")
    public OrderResponse pay(@PathVariable UUID id) {
        return OrderResponse.from(payOrder.pay(id));
    }

    @PostMapping("/{id}/cancel")
    public OrderResponse cancel(@PathVariable UUID id) {
        return OrderResponse.from(cancelOrder.cancel(id));
    }
}
