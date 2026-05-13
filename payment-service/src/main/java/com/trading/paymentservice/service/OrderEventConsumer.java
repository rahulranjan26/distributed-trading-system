package com.trading.paymentservice.service;

import com.trading.paymentservice.dto.external.OrderResponseDto;
import com.trading.paymentservice.dto.external.PaymentResponseDto;
import com.trading.paymentservice.entity.Payment;
import com.trading.paymentservice.entity.PaymentStatus;
import com.trading.paymentservice.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {


    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer paymentEventProducer;


    @KafkaListener(topics = "order.created", groupId = "payment-group")
    @Transactional
    public void consumerOrderCreatedEvent(OrderResponseDto orderResponseDto) {
        log.info("Received Order.created event for orderId:{}", orderResponseDto.getOrderId());
        boolean isSuccessful = ThreadLocalRandom.current().nextBoolean();
        log.info("orderId={}, isSuccessful={}", orderResponseDto.getOrderId(), isSuccessful);
        boolean exitingPayment = paymentRepository.existsPaymentByOrderId(orderResponseDto.getOrderId());
        if (exitingPayment) {
            log.info("Payment already exist for orderId:{}", orderResponseDto.getOrderId());
            return;
        }
        Payment payment = Payment.builder()
                .amount(orderResponseDto.getTotalAmount())
                .orderId(orderResponseDto.getOrderId())
                .userId(orderResponseDto.getUserId())
                .paymentStatus(isSuccessful ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
                .build();
        paymentRepository.save(payment);
        PaymentResponseDto paymentResponseDto = PaymentResponseDto.builder()
                .paymentId(payment.getPaymentId())
                .successful(isSuccessful)
                .orderId(payment.getOrderId())
                .totalAmount(payment.getAmount())
                .userId(payment.getUserId())
                .build();
        paymentEventProducer.sendPaymentSuccessfulEvent(paymentResponseDto);
        log.info("Payment saved for orderId:{}", orderResponseDto.getOrderId());
    }
}
