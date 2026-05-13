package com.trading.notificationservice.exceptions;


import com.trading.dto.ApiError;
import com.trading.dto.ApiResponse;
import com.trading.exception.GlobalExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class NotificationExceptionHandler extends GlobalExceptionHandler {

    @ExceptionHandler(NotificationFailedException.class)
    public ResponseEntity<ApiResponse<?>> handleNotificationFailedException(NotificationFailedException ex) {
        ApiError error = new ApiError(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ApiResponse<>(error));
    }


}
