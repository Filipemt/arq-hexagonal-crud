package com.example.orders.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** Value object imutável para dinheiro. Nunca negativo, sempre com 2 casas decimais. */
public record Money(BigDecimal amount) {

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money {
        Objects.requireNonNull(amount, "Valor é obrigatório");

        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Valor monetário não pode ser negativo");
        }
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money of(String value) {
        return new Money(new BigDecimal(value));
    }

    public Money plus(Money other) {
        return new Money(amount.add(other.amount));
    }

    /** Lança IllegalArgumentException se o resultado ficaria negativo. */
    public Money minus(Money other) {
        return new Money(amount.subtract(other.amount));
    }

    public Money times(int quantity) {
        return new Money(amount.multiply(BigDecimal.valueOf(quantity)));
    }

    public Money percentage(BigDecimal percent) {
        return new Money(amount.multiply(percent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
    }

    public boolean isGreaterThan(Money other) {
        return amount.compareTo(other.amount) > 0;
    }
}
