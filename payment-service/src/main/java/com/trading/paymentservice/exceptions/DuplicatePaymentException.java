package com.trading.paymentservice.exceptions;

public class OrderAlreadyCancelledException extends RuntimeException {
    public OrderAlreadyCancelledException(Long orderId) {
        super("Order already cancelled: " + orderId);
    }
}