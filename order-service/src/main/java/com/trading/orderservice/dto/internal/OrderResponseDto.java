package com.trading.orderservice.dto.internal;

import com.trading.orderservice.entity.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderResponseDto {
    private Long orderId;
    private Long userId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}



