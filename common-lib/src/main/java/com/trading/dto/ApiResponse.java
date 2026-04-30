package com.trading.orderservice.exceptions;

import lombok.*;

import java.time.LocalDateTime;


@Builder
@AllArgsConstructor
@Getter
@Setter
@Data
public class ApiResponse<T> {
    private T data;

    private ApiError error;

    private LocalDateTime timestamp;

    public ApiResponse(T data) {
        this();
        this.data = data;
    }

    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(ApiError error) {
        this();
        this.error = error;
    }
}
