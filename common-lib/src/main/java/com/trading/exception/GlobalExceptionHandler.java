package com.trading.exception;


import com.trading.dto.ApiError;
import com.trading.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private ApiError generateApiErrorObject(String message, HttpStatus code) {
        return ApiError.builder()
                .status(code)
                .errorMessage(message)
                .build();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        return new ResponseEntity<>(new ApiResponse<>(generateApiErrorObject(ex.getMessage(), HttpStatus.NOT_FOUND)), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<?>> handleDuplicate(DuplicateResourceException ex) {
        log.error("Duplicate resource: {}", ex.getMessage());
        return new ResponseEntity<>(new ApiResponse<>(generateApiErrorObject(ex.getMessage(), HttpStatus.CONFLICT)), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiResponse<?>> handleInsufficientStock(InsufficientStockException ex) {
        log.error("Insufficient stock: {}", ex.getMessage());
        return new ResponseEntity<>(new ApiResponse<>(generateApiErrorObject(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY)), HttpStatus.UNPROCESSABLE_ENTITY);
    }



    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiResponse<?>> handleServiceUnavailable(ServiceUnavailableException ex) {
        log.error("Service unavailable: {}", ex.getMessage());
        return new ResponseEntity<>(new ApiResponse<>(generateApiErrorObject(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE)), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("Validation failed: {}", message);
        return new ResponseEntity<>(new ApiResponse<>(generateApiErrorObject(message, HttpStatus.BAD_REQUEST)), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex) {
        log.error("Exception: {}", ex.getMessage());
        return new ResponseEntity<>(new ApiResponse<>(generateApiErrorObject(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR)), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}