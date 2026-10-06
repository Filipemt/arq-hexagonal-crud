package com.example.orders.application.port.out;

import com.example.orders.domain.model.Money;
import com.example.orders.domain.model.ProductId;
import java.util.Optional;

/** O preço vem do catálogo, nunca do cliente (evita o cliente "escolher" o preço). */
public interface ProductCatalog {
    Optional<Money> findPrice(ProductId productId);
}
