package com.trading.paymentservice.dto.external;


import lombok.*;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class PaymentResponseDto {
    private Long paymentId;
    private Long orderId;
    private Boolean successful;
    private Long userId;
    private BigDecimal totalAmount;
}
