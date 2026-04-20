package com.trading.inventoryservice.dto.internal;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductResponseDto {
    private Long productId;
    private String productName;
    private String description;
    private double price;
    private Long quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
