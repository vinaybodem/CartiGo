package com.cartigo.order.client.fallback;

import com.cartigo.order.client.CartClient;
import com.cartigo.order.dto.AddToCartRequest;
import com.cartigo.order.dto.CartResponse;
import com.cartigo.order.dto.CheckoutValidationResponse;
import org.springframework.stereotype.Component;

@Component
public class CartClientFallback implements CartClient {
    @Override
    public CheckoutValidationResponse validateCheckout() {
        throw new RuntimeException("Cart Service is temporarily unavailable for checkout validation. Circuit breaker open.");
    }

    @Override
    public CartResponse getCart() {
        throw new RuntimeException("Cart Service is temporarily unavailable. Circuit breaker open.");
    }

    @Override
    public CartResponse addItem(AddToCartRequest req) {
        throw new RuntimeException("Cart Service is temporarily unavailable to add items. Circuit breaker open.");
    }

    @Override
    public void clearCart() {
        System.err.println("Cart Service is unavailable to clear cart. Circuit breaker open.");
    }
}
