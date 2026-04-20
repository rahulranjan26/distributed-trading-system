package com.trading.paymentservice.dto.external;


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
    private BigDecimal totalAmount;
}



