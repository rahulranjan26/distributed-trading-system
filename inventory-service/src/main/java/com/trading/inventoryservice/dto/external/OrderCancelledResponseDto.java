package com.trading.inventoryservice.dto.external;


import com.trading.inventoryservice.dto.external.OrderItemRequestDto;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCancelledResponseDto {
    private Long orderId;
    private Long userId;
    private BigDecimal totalAmount;
    private List<OrderItemRequestDto> items;
}
