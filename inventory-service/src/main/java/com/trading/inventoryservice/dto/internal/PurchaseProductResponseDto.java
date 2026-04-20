package com.trading.inventoryservice.dto.internal;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PurchaseProductResponseDto {
    private Long productId;
    private Long quantity;
    private double cost;
}
