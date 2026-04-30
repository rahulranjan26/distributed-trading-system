package com.trading.paymentservice.exceptions;


import com.trading.dto.ApiError;
import com.trading.dto.ApiResponse;
import com.trading.exception.GlobalExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class PaymentExceptionHandler extends GlobalExceptionHandler {

    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<ApiResponse<?>> handlePaymentFailedException(PaymentFailedException ex) {
        ApiError error = new ApiError(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ApiResponse<>(error));
    }

    @ExceptionHandler(DuplicatePaymentException.class)
    public ResponseEntity<ApiResponse<?>> handleDuplicatePaymentException(DuplicatePaymentException ex) {
        ApiError error = new ApiError(ex.getMessage(), HttpStatus.CONFLICT);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(error));
    }

}
