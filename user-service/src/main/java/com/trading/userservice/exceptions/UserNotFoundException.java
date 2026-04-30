package com.trading.userservice.exceptions;

public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(Long orderId) {
        super("Payment failed for order: " + orderId);
    }
}