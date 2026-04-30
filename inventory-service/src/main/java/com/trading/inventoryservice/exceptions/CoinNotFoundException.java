package com.trading.inventoryservice.exceptions;

public class CoinNotFoundException extends RuntimeException {
    public CoinNotFoundException(String symbol) {
        super("Coin not found: " + symbol);
    }
}