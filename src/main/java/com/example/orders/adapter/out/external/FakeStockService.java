package com.example.orders.adapter.out.external;

import com.example.orders.application.port.out.StockService;
import com.example.orders.domain.exception.InsufficientStockException;
import com.example.orders.domain.model.OrderItem;
import com.example.orders.domain.model.ProductId;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Fake de estoque: um Map em memória. Reserva "tudo ou nada". */
@Component
public class FakeStockService implements StockService {

    private final Map<ProductId, Integer> stock = new HashMap<>();

    public FakeStockService() {
        stock.put(new ProductId(SeedData.NOTEBOOK), 5);
        stock.put(new ProductId(SeedData.MOUSE), 50);
        stock.put(new ProductId(SeedData.KEYBOARD), 20);
    }

    @Override
    public synchronized void reserve(List<OrderItem> items) {
        Map<ProductId, Integer> needed = new HashMap<>();
        for (OrderItem item : items) {
            needed.merge(item.productId(), item.quantity().value(), Integer::sum);
        }
        // 1º verifica tudo...
        needed.forEach((productId, qty) -> {
            int available = stock.getOrDefault(productId, 0);
            if (qty > available) {
                throw new InsufficientStockException(productId, qty, available);
            }
        });
        // 2º só então desconta
        needed.forEach((productId, qty) -> stock.merge(productId, -qty, Integer::sum));
        System.out.println("[STOCK] reservado: " + needed.values());
    }

    @Override
    public synchronized void release(List<OrderItem> items) {
        for (OrderItem item : items) {
            stock.merge(item.productId(), item.quantity().value(), Integer::sum);
        }
        System.out.println("[STOCK] liberado: " + items.size() + " item(ns)");
    }

    public synchronized int available(ProductId productId) {
        return stock.getOrDefault(productId, 0);
    }
}
