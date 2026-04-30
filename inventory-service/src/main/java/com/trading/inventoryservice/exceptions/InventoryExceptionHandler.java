package com.trading.inventoryservice.exceptions;

import com.trading.dto.ApiError;
import com.trading.dto.ApiResponse;
import com.trading.exception.GlobalExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class InventoryExceptionHandler extends GlobalExceptionHandler {

    @ExceptionHandler(CoinNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleCoinNotFoundException(CoinNotFoundException ex) {
        ApiError error = new ApiError(ex.getMessage(), HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(error));
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiResponse<?>> handleInsufficientStockException(InsufficientStockException ex) {
        ApiError error = new ApiError(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ApiResponse<>(error));
    }

}
