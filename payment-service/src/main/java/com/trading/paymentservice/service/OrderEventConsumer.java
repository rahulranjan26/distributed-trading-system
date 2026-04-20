package com.trading.paymentservice.service;

import com.trading.paymentservice.dto.external.OrderResponseDto;
import com.trading.paymentservice.entity.Payment;
import com.trading.paymentservice.entity.PaymentStatus;
import com.trading.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {


    private final PaymentRepository paymentRepository;

    @KafkaListener(topics = "order.created", groupId = "payment-group")
    public void consumerOrderCreatedEvent(OrderResponseDto orderResponseDto) {
        log.info("Received Order.created event for orderId:{}", orderResponseDto.getOrderId());

        Payment payment = Payment.builder()
                .amount(orderResponseDto.getTotalAmount())
                .orderId(orderResponseDto.getOrderId())
                .userId(orderResponseDto.getUserId())
                .paymentStatus(PaymentStatus.SUCCESSFUL)
                .build();
        paymentRepository.save(payment);
        log.info("Payment saved for orderId:{}", orderResponseDto.getOrderId());
    }
}
