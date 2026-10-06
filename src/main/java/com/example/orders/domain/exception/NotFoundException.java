package com.example.orders.domain.exception;

public class NotFoundException extends DomainException {
    public NotFoundException(String message) {
        super(message);
    }
}
