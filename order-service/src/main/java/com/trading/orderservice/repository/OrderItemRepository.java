package com.trading.orderservice.repository;

import com.trading.orderservice.entity.Order;
import com.trading.orderservice.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {

    List<OrderItem> findOrderItemByOrder(Order order);
}
