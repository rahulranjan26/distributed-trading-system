package com.trading.orderservice.service;


import com.trading.orderservice.dto.external.OrderCancelledResponseDto;
import com.trading.orderservice.dto.external.PaymentResponseDto;
import com.trading.orderservice.dto.internal.OrderItemRequestDto;
import com.trading.orderservice.dto.internal.OrderResponseDto;
import com.trading.orderservice.entity.Order;
import com.trading.orderservice.entity.OrderItem;
import com.trading.orderservice.entity.OrderStatus;
import com.trading.orderservice.repository.OrderItemRepository;
import com.trading.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class PaymentEventConsumer {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderEventProducer orderEventProducer;

    @KafkaListener(topics = "payment.done", groupId = "order-group")
    @Transactional
    public void consumePaymentCreatedEvent(PaymentResponseDto paymentResponseDto) {
        log.info("We are consuming the payment event for Order :{}", paymentResponseDto.getOrderId());
        Optional<Order> savedOrder = orderRepository.findById(paymentResponseDto.getOrderId());
        if (savedOrder.isPresent()) {
            Order presentOrder = savedOrder.get();
            if (paymentResponseDto.getSuccessful()) {
                presentOrder.setStatus(OrderStatus.SUCCESSFUL);
                orderRepository.save(presentOrder);
            } else {
                presentOrder.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(presentOrder);

                List<OrderItem> items = orderItemRepository.findOrderItemByOrder(presentOrder);
                List<OrderItemRequestDto> orderItemRequestDtos = items.stream().map(m -> {
                    OrderItemRequestDto od = OrderItemRequestDto.builder()
                            .quantity(Math.toIntExact(m.getQuantity()))
                            .productId(m.getProductId())
                            .build();
                    return od;
                }).toList();
                OrderCancelledResponseDto cancelledResponseDto = OrderCancelledResponseDto.builder()
                        .userId(presentOrder.getUserId())
                        .orderId(presentOrder.getOrderId())
                        .totalAmount(presentOrder.getTotalAmount())
                        .items(orderItemRequestDtos)
                        .build();
                orderEventProducer.orderCancelledEvent(cancelledResponseDto);
            }
        }
    }

}
