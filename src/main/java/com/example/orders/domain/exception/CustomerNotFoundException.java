package com.example.orders.domain.exception;

import com.example.orders.domain.model.CustomerId;

public class CustomerNotFoundException extends NotFoundException {
    public CustomerNotFoundException(CustomerId id) {
        super("Cliente não encontrado: " + id.value());
    }
}
