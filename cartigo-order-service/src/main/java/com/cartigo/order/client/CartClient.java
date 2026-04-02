package com.cartigo.order.client;

import com.cartigo.order.dto.CartResponse;
import com.cartigo.order.dto.CheckoutValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.*;

@FeignClient(name = "CART-SERVICE",configuration = FeignConfig.class)
public interface CartClient {

    @PostMapping("/api/cart/order/checkout/validate")
    CheckoutValidationResponse validateCheckout();

    @GetMapping("/api/cart/get")
    CartResponse getCart();

    @DeleteMapping("/api/cart/clear")
    void clearCart();
}