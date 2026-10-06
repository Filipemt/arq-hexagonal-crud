package com.example.orders.application.port.out;

public record PaymentResult(boolean approved, String reason) {
    public static PaymentResult ok() {
        return new PaymentResult(true, null);
    }

    public static PaymentResult declined(String reason) {
        return new PaymentResult(false, reason);
    }
}
