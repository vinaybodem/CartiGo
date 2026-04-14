package com.cartigo.order.client;

import com.cartigo.order.client.fallback.InventoryClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "INVENTORY-SERVICE",configuration = FeignConfig.class,fallback = InventoryClientFallback.class)
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
