package com.trading.inventoryservice.dto.external;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InventoryResponse {
    private Long productId;
    private BigDecimal price;
    private boolean available;
}
