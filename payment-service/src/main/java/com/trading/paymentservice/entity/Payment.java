package com.trading.paymentservice.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name="payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Enumerated(value = EnumType.STRING)
    private PaymentStatus paymentStatus;

    private Long userId;

    private Long orderId;

    private BigDecimal amount;

    @CreationTimestamp
    private LocalDateTime createdAt;


}
