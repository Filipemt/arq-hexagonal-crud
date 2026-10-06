package com.example.orders.adapter.out.external;

import com.example.orders.application.port.out.ProductCatalog;
import com.example.orders.domain.model.Money;
import com.example.orders.domain.model.ProductId;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/** Fake do catálogo de produtos. Em produção poderia ser uma API HTTP ou outro serviço. */
@Component
public class InMemoryProductCatalog implements ProductCatalog {

    private final Map<ProductId, Money> prices = Map.of(
            new ProductId(SeedData.NOTEBOOK), Money.of("3500.00"),
            new ProductId(SeedData.MOUSE),    Money.of("80.00"),
            new ProductId(SeedData.KEYBOARD), Money.of("250.00"));

    @Override
    public Optional<Money> findPrice(ProductId productId) {
        return Optional.ofNullable(prices.get(productId));
    }
}
