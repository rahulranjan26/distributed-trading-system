package com.trading.orderservice.controller;


import com.trading.orderservice.dto.internal.OrderRequest;
import com.trading.orderservice.dto.internal.OrderResponseDto;
import com.trading.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(path = "/orders")
@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping(path = "/orderitems")
    ResponseEntity<OrderResponseDto> orderItems(@RequestBody OrderRequest request, @RequestHeader("X-User-Id") Long userId) {
        request.setUserId(userId);
        log.info(request.toString());
        return orderService.orderItems(request);
    }

}
