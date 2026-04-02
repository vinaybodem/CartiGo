package com.cartigo.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.*;

@FeignClient(name = "INVENTORY-SERVICE",configuration = FeignConfig.class)
public interface InventoryClient {

    @PostMapping("/api/inventory/{productId}/reserve")
    void reserve(
            @PathVariable Long productId,
            @RequestParam Integer qty
    );

    @PostMapping("/api/inventory/{productId}/reduce")
    void reduce(
            @PathVariable Long productId,
            @RequestParam Integer qty
    );

    @PostMapping("/api/inventory/{productId}/release")
    void release(
            @PathVariable Long productId,
            @RequestParam Integer qty
    );
}
