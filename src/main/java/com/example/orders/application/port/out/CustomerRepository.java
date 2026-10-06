package com.example.orders.application.port.out;

import com.example.orders.domain.model.Customer;
import com.example.orders.domain.model.CustomerId;
import java.util.Optional;

public interface CustomerRepository {
    Optional<Customer> findById(CustomerId id);
}
