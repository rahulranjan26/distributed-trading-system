package com.trading.inventoryservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PurchaseProductRequestDto {
    private Long productId;
    private Long quantity;
}
