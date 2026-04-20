package com.trading.inventoryservice.dto.external;


import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InventoryRequest {
    private Long productId;
    private int quantity;
}
