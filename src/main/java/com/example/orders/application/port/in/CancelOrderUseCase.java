package com.example.orders.application.port.in;

import com.example.orders.domain.model.Order;
import java.util.UUID;

public interface CancelOrderUseCase {
    Order cancel(UUID orderId);
}
