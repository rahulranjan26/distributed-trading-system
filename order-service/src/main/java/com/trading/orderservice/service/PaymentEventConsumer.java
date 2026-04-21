package com.trading.orderservice.service;


import com.trading.orderservice.dto.external.PaymentResponseDto;
import com.trading.orderservice.entity.Order;
import com.trading.orderservice.entity.OrderStatus;
import com.trading.orderservice.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class PaymentEventConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "payment.done",groupId = "order-group")
    public void consumePaymentCreatedEvent(PaymentResponseDto paymentResponseDto){
        log.info("We are consuming the payment event for Order :{}",paymentResponseDto.getOrderId());
        Optional<Order> savedOrder = orderRepository.findById(paymentResponseDto.getOrderId());
        if (savedOrder.isPresent()){
            Order presentOrder = savedOrder.get();
            if (paymentResponseDto.getSuccessful()){
                presentOrder.setStatus(OrderStatus.SUCCESSFUL);
                orderRepository.save(presentOrder);
            }else {
                presentOrder.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(presentOrder);
            }
        }
    }

}
