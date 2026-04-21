package com.trading.paymentservice.service;

import com.trading.paymentservice.dto.external.OrderResponseDto;
import com.trading.paymentservice.dto.external.PaymentResponseDto;
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
    private final PaymentEventProducer paymentEventProducer;

    @KafkaListener(topics = "order.created", groupId = "payment-group")
    public void consumerOrderCreatedEvent(OrderResponseDto orderResponseDto) {
        log.info("Received Order.created event for orderId:{}", orderResponseDto.getOrderId());
        boolean exitingPayment = paymentRepository.existsPaymentByOrderId(orderResponseDto.getOrderId());
        if (exitingPayment) {
            log.info("Payment already exist for orderId:{}", orderResponseDto.getOrderId());
            return;
        }
        Payment payment = Payment.builder()
                .amount(orderResponseDto.getTotalAmount())
                .orderId(orderResponseDto.getOrderId())
                .userId(orderResponseDto.getUserId())
                .paymentStatus(PaymentStatus.SUCCESSFUL)
                .build();
        paymentRepository.save(payment);
        PaymentResponseDto paymentResponseDto = PaymentResponseDto.builder()
                .paymentId(payment.getPaymentId())
                .successful(true)
                .orderId(payment.getOrderId())
                .build();
        paymentEventProducer.sendPaymentSuccessfulEvent(paymentResponseDto);
        log.info("Payment saved for orderId:{}", orderResponseDto.getOrderId());
    }
}
