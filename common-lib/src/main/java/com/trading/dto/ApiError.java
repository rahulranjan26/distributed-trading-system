package com.trading.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@Getter
@Setter
public class ApiError {
    private String errorMessage;
    private HttpStatus status;
    private LocalDateTime timestamp;

    public ApiError() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiError(String errorMessage, HttpStatus status) {
        this();
        this.errorMessage = errorMessage;
        this.status = status;

    }
}
