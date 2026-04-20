package com.trading.orderservice.repository;

import com.trading.orderservice.entity.Order;
import com.trading.orderservice.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
