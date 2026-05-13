package com.trading.orderservice.exceptions;


import com.trading.dto.ApiError;
import com.trading.dto.ApiResponse;
import com.trading.exception.GlobalExceptionHandler;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class OrderExceptionHandler extends GlobalExceptionHandler {
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleOrderNotFoundException(OrderNotFoundException ex) {
        ApiError error = new ApiError(ex.getMessage(), HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(error));
    }

    @ExceptionHandler(OrderAlreadyCancelledException.class)
    public ResponseEntity<ApiResponse<?>> handleOrderAlreadyCancelledException(OrderAlreadyCancelledException ex) {
        ApiError error = new ApiError(ex.getMessage(), HttpStatus.CONFLICT);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(error));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse<?>> handleFeignException(FeignException ex) {
        ApiError error = new ApiError(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ApiResponse<>(error));
    }

}
