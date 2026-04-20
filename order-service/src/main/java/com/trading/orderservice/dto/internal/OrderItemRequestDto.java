package com.trading.orderservice.dto.internal;


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
