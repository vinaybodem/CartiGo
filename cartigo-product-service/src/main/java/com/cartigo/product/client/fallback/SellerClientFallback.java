package com.cartigo.product.client.fallback;

import com.cartigo.product.client.SellerClient;
import com.cartigo.product.dto.SellerDto;
import org.springframework.stereotype.Component;

@Component
public class SellerClientFallback implements SellerClient {
    @Override
    public SellerDto getSellerById(Long user_id) {
        throw new RuntimeException("User Service is temporarily unavailable to fetch seller details. Circuit breaker open.");
    }
}
