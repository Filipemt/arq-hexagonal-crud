package com.example.orders.adapter.in.web;

import com.example.orders.domain.exception.DomainException;
import com.example.orders.domain.exception.NotFoundException;
import com.example.orders.domain.exception.PaymentFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Traduz exceções do domínio em status HTTP. Isso é responsabilidade do ADAPTER:
 * o domínio não sabe o que é HTTP 404 ou 409.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<Map<String, String>> notFound(NotFoundException e) {
        return body(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(PaymentFailedException.class)
    ResponseEntity<Map<String, String>> paymentFailed(PaymentFailedException e) {
        return body(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
    }

    /** Estado inválido, estoque insuficiente etc. */
    @ExceptionHandler(DomainException.class)
    ResponseEntity<Map<String, String>> businessRule(DomainException e) {
        return body(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Map<String, String>> invalidArgument(IllegalArgumentException e) {
        return body(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, String>> invalidBody(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Requisição inválida");
        return body(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseEntity<Map<String, String>> body(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }
}
