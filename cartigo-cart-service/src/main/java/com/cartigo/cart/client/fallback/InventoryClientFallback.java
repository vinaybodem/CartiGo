package com.cartigo.cart.client.fallback;

import com.cartigo.cart.client.InventoryClient;
import org.springframework.stereotype.Component;

@Component
public class InventoryClientFallback implements InventoryClient {
    @Override
    public Boolean checkAvailability(Long productId, Integer quantity) {
        // Conservative approach: return false if inventory service is down
        return false;
    }
}
