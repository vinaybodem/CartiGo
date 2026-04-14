package com.cartigo.order.client.fallback;

import com.cartigo.order.client.InventoryClient;
import org.springframework.stereotype.Component;

@Component
public class InventoryClientFallback implements InventoryClient {
    @Override
    public void reserve(Long productId, Integer qty) {
        throw new RuntimeException("Inventory Service is unavailable for reservation. Circuit breaker open.");
    }

    @Override
    public void reduce(Long productId, Integer qty) {
        throw new RuntimeException("Inventory Service is unavailable for stock reduction. Circuit breaker open.");
    }

    @Override
    public void release(Long productId, Integer qty) {
        System.err.println("Inventory Service is unavailable for releasing stock. Circuit breaker open.");
    }
}
