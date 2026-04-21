package com.trading.paymentservice.dto.external;


import lombok.*;

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
}
