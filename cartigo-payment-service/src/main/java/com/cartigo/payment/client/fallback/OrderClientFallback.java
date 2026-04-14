package com.cartigo.payment.client.fallback;


import com.cartigo.payment.client.OrderClient;
import org.springframework.stereotype.Component;

@Component
public class OrderClientFallback implements OrderClient {
    @Override
    public void confirmOrder(Long orderId) {
        throw new RuntimeException("Order Service is unavailable to confirm order. Circuit breaker open.");
    }
}
