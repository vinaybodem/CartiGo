package com.cartigo.cart.controller;

import com.cartigo.cart.dto.AddToCartRequest;
import com.cartigo.cart.dto.CartResponse;
import com.cartigo.cart.dto.CheckoutValidationResponse;
import com.cartigo.cart.dto.UpdateCartItemRequest;
import com.cartigo.cart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    // Fetch cart
    @GetMapping("/get/{userId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getCart(userId));
    }

    // Add item to cart
    @PostMapping("/add/items/{userId}")
    public ResponseEntity<CartResponse> addItem(
            @PathVariable Long userId,
            @Valid @RequestBody AddToCartRequest req) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.addItem(userId, req));
    }

    // Update quantity
    @PutMapping("/update/{userId}/items/{productId}")
    public ResponseEntity<CartResponse> updateItem(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest req) {

        return ResponseEntity.ok(service.updateItem(userId, productId, req));
    }

    // Remove item from cart
    @DeleteMapping("/delete/{userId}/items/{productId}")
    public ResponseEntity<CartResponse> removeItem(
            @PathVariable Long userId,
            @PathVariable Long productId) {

        return ResponseEntity.ok(service.removeItem(userId, productId));
    }

    // Clear entire cart
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {

        service.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    // Validate checkout before placing order
    @PostMapping("/{userId}/order/checkout/validate")
    public ResponseEntity<CheckoutValidationResponse> validateCheckout(
            @PathVariable Long userId) {

        return ResponseEntity.ok(service.validateCheckout(userId));
    }
}