package com.trading.inventoryservice.dto.external;


import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequestDto {
    private Long productId;
    private int quantity;
}
