package com.cartigo.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ORDER-SERVICE")
public interface OrderClient {

    @PostMapping("/api/orders/confirm/{orderId}")
    void confirmOrder(@PathVariable("orderId") Long orderId);
}