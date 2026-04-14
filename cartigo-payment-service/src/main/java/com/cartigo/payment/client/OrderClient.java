package com.cartigo.payment.client;

import com.cartigo.payment.client.fallback.OrderClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "ORDER-SERVICE",fallback = OrderClientFallback.class)
public interface OrderClient {

    @PostMapping("/api/orders/confirm/{orderId}")
    void confirmOrder(@PathVariable("orderId") Long orderId);
}