package com.cartigo.order.controller;

import com.cartigo.order.dto.OrderCreateRequest;
import com.cartigo.order.dto.OrderResponse;
import com.cartigo.order.entity.Order;
import com.cartigo.order.entity.OrderStatus;
import com.cartigo.order.mapper.OrderMapper;
import com.cartigo.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderCreateRequest req) {
        Order o = service.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderMapper.toOrder(o, service.items(o.getId())));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> listByUser(@PathVariable Long userId) {
        List<OrderResponse> out = service.listByUser(userId).stream()
                .map(o -> OrderMapper.toOrder(o, service.items(o.getId())))
                .toList();
        return ResponseEntity.ok(out);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> get(@PathVariable Long orderId) {
        Order o = service.get(orderId);
        return ResponseEntity.ok(OrderMapper.toOrder(o, service.items(o.getId())));
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> setStatus(@PathVariable Long orderId, @RequestParam OrderStatus value) {
        Order o = service.setStatus(orderId, value);
        return ResponseEntity.ok(OrderMapper.toOrder(o, service.items(o.getId())));
    }

    // Internal for Review + Return/Refund
    @GetMapping("/verify-purchase")
    public ResponseEntity<Boolean> verifyPurchase(@RequestParam Long userId, @RequestParam Long productId) {
        return ResponseEntity.ok(service.verifyPurchase(userId, productId));
    }
}
