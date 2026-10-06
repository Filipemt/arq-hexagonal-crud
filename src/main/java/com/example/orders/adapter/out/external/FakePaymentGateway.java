package com.example.orders.adapter.out.external;

import com.example.orders.application.port.out.PaymentGateway;
import com.example.orders.application.port.out.PaymentResult;
import com.example.orders.domain.model.Money;
import com.example.orders.domain.model.OrderId;
import org.springframework.stereotype.Component;

/** Fake de gateway de pagamento: aprova tudo até R$ 5.000,00 e recusa acima disso. */
@Component
public class FakePaymentGateway implements PaymentGateway {

    private static final Money LIMIT = Money.of("5000.00");

    @Override
    public PaymentResult charge(OrderId orderId, Money amount) {
        if (amount.isGreaterThan(LIMIT)) {
            System.out.println("[PAYMENT] recusado " + amount.amount() + " (pedido " + orderId.value() + ")");
            return PaymentResult.declined("limite do cartão excedido (fake)");
        }
        System.out.println("[PAYMENT] aprovado " + amount.amount() + " (pedido " + orderId.value() + ")");
        return PaymentResult.ok();
    }

    @Override
    public void refund(OrderId orderId, Money amount) {
        System.out.println("[PAYMENT] estorno " + amount.amount() + " (pedido " + orderId.value() + ")");
    }
}
