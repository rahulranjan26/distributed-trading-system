package com.trading.paymentservice.exceptions;

public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(Long orderId) {
        super("Payment failed for order: " + orderId);
    }
}