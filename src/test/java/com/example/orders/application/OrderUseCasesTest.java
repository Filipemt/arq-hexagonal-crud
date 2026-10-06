package com.example.orders.application;

import com.example.orders.adapter.out.events.LoggingEventPublisher;
import com.example.orders.adapter.out.external.*;
import com.example.orders.adapter.out.persistence.InMemoryCustomerRepository;
import com.example.orders.adapter.out.persistence.InMemoryOrderRepository;
import com.example.orders.application.port.in.PlaceOrderCommand;
import com.example.orders.application.port.out.EventPublisher;
import com.example.orders.application.port.out.PaymentGateway;
import com.example.orders.application.port.out.PaymentResult;
import com.example.orders.application.service.CancelOrderService;
import com.example.orders.application.service.PayOrderService;
import com.example.orders.application.service.PlaceOrderService;
import com.example.orders.domain.event.DomainEvent;
import com.example.orders.domain.exception.CustomerNotFoundException;
import com.example.orders.domain.exception.InsufficientStockException;
import com.example.orders.domain.exception.PaymentFailedException;
import com.example.orders.domain.model.*;
import com.example.orders.domain.service.DiscountPolicy;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testa os casos de uso SEM Spring: basta instanciar os services passando fakes das ports.
 * Esta é a grande vantagem prática da arquitetura hexagonal.
 */
class OrderUseCasesTest {

    private final InMemoryOrderRepository orders = new InMemoryOrderRepository();
    private final InMemoryCustomerRepository customers = new InMemoryCustomerRepository();
    private final InMemoryProductCatalog catalog = new InMemoryProductCatalog();
    private final FakeStockService stock = new FakeStockService();
    private final RecordingPaymentGateway payments = new RecordingPaymentGateway();
    private final List<DomainEvent> published = new ArrayList<>();
    private final EventPublisher publisher = published::addAll;

    private final PlaceOrderService placeOrder =
            new PlaceOrderService(orders, customers, catalog, stock, publisher, new DiscountPolicy());
    private final PayOrderService payOrder = new PayOrderService(orders, payments, publisher);
    private final CancelOrderService cancelOrder = new CancelOrderService(orders, stock, payments, publisher);

    private PlaceOrderCommand command(UUID customer, UUID product, int qty) {
        return new PlaceOrderCommand(customer, List.of(new PlaceOrderCommand.Item(product, qty)));
    }

    @Test
    void clienteVipRecebeDescontoEEventoEPublicado() {
        Order order = placeOrder.place(command(InMemoryCustomerRepository.ANA_VIP, SeedData.KEYBOARD, 1));

        assertEquals(Money.of("25.00"), order.getDiscount());   // 10% de 250
        assertEquals(Money.of("225.00"), order.total());
        assertEquals(1, published.size());
        assertEquals(19, stock.available(new ProductId(SeedData.KEYBOARD)));
    }

    @Test
    void estoqueInsuficienteFalhaENaoReservaNada() {
        assertThrows(InsufficientStockException.class,
                () -> placeOrder.place(command(InMemoryCustomerRepository.BRUNO_REGULAR, SeedData.NOTEBOOK, 6)));

        assertEquals(5, stock.available(new ProductId(SeedData.NOTEBOOK)));
        assertTrue(published.isEmpty());
    }

    @Test
    void clienteInexistenteFalha() {
        assertThrows(CustomerNotFoundException.class,
                () -> placeOrder.place(command(UUID.randomUUID(), SeedData.MOUSE, 1)));
    }

    @Test
    void pagamentoRecusadoNaoAlteraOStatusDoPedido() {
        payments.approve = false;
        Order order = placeOrder.place(command(InMemoryCustomerRepository.BRUNO_REGULAR, SeedData.MOUSE, 1));

        assertThrows(PaymentFailedException.class, () -> payOrder.pay(order.getId().value()));
        assertEquals(OrderStatus.CREATED, order.getStatus());
    }

    @Test
    void cancelarPedidoPagoEstornaELiberaEstoque() {
        Order order = placeOrder.place(command(InMemoryCustomerRepository.BRUNO_REGULAR, SeedData.MOUSE, 2));
        payOrder.pay(order.getId().value());
        assertEquals(48, stock.available(new ProductId(SeedData.MOUSE)));

        cancelOrder.cancel(order.getId().value());

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(1, payments.refunds.size());
        assertEquals(50, stock.available(new ProductId(SeedData.MOUSE)));
    }

    @Test
    void cancelarPedidoNaoPagoNaoGeraEstorno() {
        Order order = placeOrder.place(command(InMemoryCustomerRepository.BRUNO_REGULAR, SeedData.MOUSE, 1));

        cancelOrder.cancel(order.getId().value());

        assertTrue(payments.refunds.isEmpty());
    }

    /** Fake escrito à mão no teste: grava as chamadas de estorno. */
    private static class RecordingPaymentGateway implements PaymentGateway {
        boolean approve = true;
        final List<OrderId> refunds = new ArrayList<>();

        @Override
        public PaymentResult charge(OrderId orderId, Money amount) {
            return approve ? PaymentResult.ok() : PaymentResult.declined("recusado no teste");
        }

        @Override
        public void refund(OrderId orderId, Money amount) {
            refunds.add(orderId);
        }
    }
}
