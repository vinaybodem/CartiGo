package com.cartigo.cart.controller;

import com.cartigo.cart.config.SecurityUtils;
import com.cartigo.cart.dto.*;
import com.cartigo.cart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService service;
    public CartController(CartService service ) {
        this.service = service;

    }

    // Fetch cart
    @GetMapping("/get")
    public ResponseEntity<CartResponse> getCart() {
        Long userId =SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(service.getCart(userId));
    }

    // Add item to cart
    @PostMapping("/add/items")
    public ResponseEntity<CartResponse> addItem(
            @Valid @RequestBody AddToCartRequest req) {
        Long userId =SecurityUtils.getCurrentUserId();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.addItem(userId, req));
    }

    // Update quantity
    @PutMapping("/update/items/{productId}")
    public ResponseEntity<CartResponse> updateItem(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest req) {
        Long userId =SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(service.updateItem(userId, productId, req));
    }

    // Remove item from cart
    @DeleteMapping("/delete/items/{productId}")
    public ResponseEntity<CartResponse> removeItem(
            @PathVariable Long productId) {
        Long userId =SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(service.removeItem(userId, productId));
    }

    // Clear entire cart
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart() {
        Long userId =SecurityUtils.getCurrentUserId();
        service.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    // Validate checkout before placing order
    @PostMapping("/order/checkout/validate")
    public ResponseEntity<CheckoutValidationResponse> validateCheckout() {
        Long userId =SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(service.validateCheckout(userId));
    }
}