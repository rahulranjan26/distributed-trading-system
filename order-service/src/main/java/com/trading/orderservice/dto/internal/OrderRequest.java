package com.trading.orderservice.dto.internal;


import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private Long userId;
    private List<OrderItemRequestDto> items;
}
