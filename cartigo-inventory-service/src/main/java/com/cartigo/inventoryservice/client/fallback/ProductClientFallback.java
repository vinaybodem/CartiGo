package com.cartigo.inventoryservice.client.fallback;

import com.cartigo.inventoryservice.client.ProductClient;
import com.cartigo.inventoryservice.dao.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductClientFallback implements ProductClient {
    @Override
    public ProductResponse validate(Long prod_id) {
        throw new RuntimeException("Product Service is temporarily unavailable. Circuit breaker open.");
    }
}
