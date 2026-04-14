package com.cartigo.cart.client.fallback;

import com.cartigo.cart.client.ProductClient;
import com.cartigo.cart.dto.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductClientFallback implements ProductClient {
    @Override
    public ProductResponse getProduct(Long prod_id) {
        throw new RuntimeException("Product Service is temporarily unavailable. Circuit breaker open.");
    }
}

