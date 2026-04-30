package com.trading.inventoryservice.exceptions;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String symbol) {
        super("Insufficient stock for: " + symbol);
    }
}