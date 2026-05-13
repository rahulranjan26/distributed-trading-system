package com.trading.orderservice.service;


import com.trading.orderservice.clients.InventoryClient;
import com.trading.orderservice.dto.external.InventoryRequest;
import com.trading.orderservice.dto.external.InventoryResponse;
import com.trading.orderservice.dto.internal.OrderRequest;
import com.trading.orderservice.dto.internal.OrderResponseDto;
import com.trading.orderservice.entity.Order;
import com.trading.orderservice.entity.OrderItem;
import com.trading.orderservice.entity.OrderStatus;
import com.trading.orderservice.repository.OrderItemRepository;
import com.trading.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@RequiredArgsConstructor
@Service
@Slf4j
public class OrderService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final OrderEventProducer orderEventProducer;

    @Transactional
    public ResponseEntity<OrderResponseDto> orderItems(OrderRequest request) {
        log.info("creating orders in OrderService");
        log.info("X-User-Id: {}", request.getUserId());
        List<InventoryRequest> inventoryRequest = new ArrayList<>();

        for (var res : request.getItems()) {
            InventoryRequest ir = InventoryRequest.builder()
                    .quantity(res.getQuantity())
                    .productId(res.getProductId())
                    .build();
            inventoryRequest.add(ir);
        }

        List<InventoryResponse> inventoryResponse = inventoryClient.checkProduct(inventoryRequest);

        BigDecimal totalAmount = BigDecimal.valueOf(0);
        HashMap<Long, BigDecimal> mapping = new HashMap<>();

        for (var v : inventoryResponse) {
            if (v.isAvailable()) {
                mapping.put(v.getProductId(), v.getPrice());
            }
        }

        for (var r : request.getItems()) {
            BigDecimal price = mapping.get(r.getProductId());
            price = price.multiply(BigDecimal.valueOf(r.getQuantity()));
            totalAmount = totalAmount.add(price);
        }

        Order order = Order
                .builder()
                .userId(request.getUserId())
                .status(OrderStatus.CREATED)
                .totalAmount(totalAmount)
                .build();

        Order savedOrder = orderRepository.save(order);

        for (var v : request.getItems()) {
            OrderItem item = OrderItem.builder()
                    .order(savedOrder)
                    .quantity((long) v.getQuantity())
                    .price(mapping.get(v.getProductId()))
                    .productId(v.getProductId())
                    .build();
            orderItemRepository.save(item);
        }
        OrderResponseDto responseDto = OrderResponseDto.builder()
                .orderId(savedOrder.getOrderId())
                .userId(savedOrder.getUserId())
                .status(savedOrder.getStatus())
                .totalAmount(savedOrder.getTotalAmount())
                .build();
        orderEventProducer.sendOrderCreatedEvent(responseDto);
        return ResponseEntity.ok(responseDto);
    }
}
