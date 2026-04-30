package com.trading.userservice.exceptions;

public class DuplicatePaymentException extends RuntimeException {
    public DuplicatePaymentException(Long orderId) {
        super("Payment already processed for order: " + orderId);
    }
}