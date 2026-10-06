package com.example.orders.adapter.out.persistence;

import com.example.orders.application.port.out.CustomerRepository;
import com.example.orders.domain.model.Customer;
import com.example.orders.domain.model.CustomerId;
import com.example.orders.domain.model.CustomerType;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Adapter outbound (mock) com dois clientes fixos para facilitar os testes manuais. */
@Component
public class InMemoryCustomerRepository implements CustomerRepository {

    public static final UUID ANA_VIP = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final UUID BRUNO_REGULAR = UUID.fromString("22222222-2222-2222-2222-222222222222");

    private final Map<CustomerId, Customer> customers = Map.of(
            new CustomerId(ANA_VIP),
            new Customer(new CustomerId(ANA_VIP), "Ana", CustomerType.VIP),
            new CustomerId(BRUNO_REGULAR),
            new Customer(new CustomerId(BRUNO_REGULAR), "Bruno", CustomerType.REGULAR));

    @Override
    public Optional<Customer> findById(CustomerId id) {
        return Optional.ofNullable(customers.get(id));
    }
}
