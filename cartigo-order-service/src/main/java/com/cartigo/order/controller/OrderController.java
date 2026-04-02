package com.cartigo.order.controller;

import com.cartigo.order.config.SecurityUtils;
import com.cartigo.order.dto.OrderResponse;
import com.cartigo.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder() {

        Long userId = SecurityUtils.getCurrentUserId();

        return ResponseEntity.ok(
                service.placeOrder(userId)
        );
    }

    @GetMapping("/my-orders")
    public ResponseEntity<?> myOrders() {

        Long userId = SecurityUtils.getCurrentUserId();

        return ResponseEntity.ok(
                service.getOrders(userId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> orderDetails(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.getOrder(id)
        );
    }
    @PostMapping("/confirm/{orderId}")
    public void confirmOrder(@PathVariable Long orderId){

        service.confirmOrder(orderId);
    }
}