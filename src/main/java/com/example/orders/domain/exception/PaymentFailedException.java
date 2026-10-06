package com.example.orders.domain.exception;

public class PaymentFailedException extends DomainException {
    public PaymentFailedException(String reason) {
        super("Pagamento recusado: " + reason);
    }
}
