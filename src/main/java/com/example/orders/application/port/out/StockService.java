package com.example.orders.application.port.out;

import com.example.orders.domain.model.OrderItem;
import java.util.List;

public interface StockService {
    /** Reserva todos os itens ou nenhum. Lança InsufficientStockException se faltar estoque. */
    void reserve(List<OrderItem> items);

    void release(List<OrderItem> items);
}
