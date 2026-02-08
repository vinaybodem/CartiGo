package com.cartigo.review.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${order.service.name:cartigo-order-service}")
public interface OrderClient {

    @GetMapping("/api/orders/verify-purchase")
    Boolean verifyPurchase(@RequestParam("userId") Long userId,
                           @RequestParam("productId") Long productId);
}
