package com.example.orders.domain.exception;

/** Base das exceções de negócio. O adapter web decide qual status HTTP cada uma vira. */
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
