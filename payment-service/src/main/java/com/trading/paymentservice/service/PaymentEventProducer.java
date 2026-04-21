package com.trading.paymentservice.service;

import com.trading.paymentservice.dto.external.PaymentResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String,Object> kafkaTemplate;

    public void sendPaymentSuccessfulEvent(PaymentResponseDto paymentResponseDto){
        log.info("Sending payment successful event for orderId : {}",paymentResponseDto.getOrderId());
        kafkaTemplate.send("payment.done",paymentResponseDto.getPaymentId().toString(),paymentResponseDto);
    }
}
