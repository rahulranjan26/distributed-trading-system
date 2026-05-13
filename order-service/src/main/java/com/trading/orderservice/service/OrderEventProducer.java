package com.trading.orderservice.service;

import com.trading.orderservice.dto.external.OrderCancelledResponseDto;
import com.trading.orderservice.dto.internal.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderCreatedEvent(OrderResponseDto orderResponseDto) {
        log.info("Sending order.created event for OrderId :{}", orderResponseDto.getOrderId());
        kafkaTemplate.send("order.created", orderResponseDto.getOrderId().toString(), orderResponseDto);
    }


    public void orderCancelledEvent(OrderCancelledResponseDto orderCancelledResponseDto){
        log.info("Order got cancelled so need to restore the stocks:{}",orderCancelledResponseDto.getOrderId());
        kafkaTemplate.send("order.cancelled",orderCancelledResponseDto.getOrderId().toString(),orderCancelledResponseDto);
    }
}
