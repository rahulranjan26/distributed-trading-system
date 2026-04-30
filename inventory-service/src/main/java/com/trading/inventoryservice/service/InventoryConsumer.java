package com.trading.inventoryservice.service;


import com.trading.inventoryservice.dto.external.OrderCancelledResponseDto;
import com.trading.inventoryservice.dto.external.OrderItemRequestDto;
import com.trading.inventoryservice.dto.internal.ProductResponseDto;
import com.trading.inventoryservice.entity.Product;
import com.trading.inventoryservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryConsumer {


    private final ProductRepository productRepository;
    private final RedissonClient client;

    @KafkaListener(topics = "order.cancelled", groupId = "inventory-group")
    @Transactional
    public void orderCancelledConsumer(OrderCancelledResponseDto orderCancelledResponseDto) {
        log.info("Restocking the supply for failed order : {}", orderCancelledResponseDto.getOrderId());
        RLock lock = client.getLock("orderId" + orderCancelledResponseDto.getOrderId());
        for (var item : orderCancelledResponseDto.getItems()) {
            lock.lock();
            try {
                Product p = productRepository.findById(item.getProductId()).orElse(null);
                if (p == null)
                    continue;
                p.setQuantity(p.getQuantity() + item.getQuantity());
                productRepository.save(p);
            } finally {
                lock.unlock();
            }
        }
    }
}
