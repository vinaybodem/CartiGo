package com.cartigo.cart.client;

import com.cartigo.cart.client.fallback.InventoryClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "INVENTORY-SERVICE",fallback = InventoryClientFallback.class)
public interface InventoryClient {

    @GetMapping("/api/inventory/available/{productId}")
    Boolean checkAvailability(@PathVariable("productId") Long productId,
                              @RequestParam("quantity") Integer quantity);

}