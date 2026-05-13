package com.trading.notificationservice.exceptions;

public class NotificationFailedException extends RuntimeException {
    public NotificationFailedException(Long userId) {
        super("Failed to send notification to user: " + userId);
    }
}