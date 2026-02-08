package com.cartigo.returns.service;

public interface PurchaseVerifier {
    boolean hasPurchased(Long userId, Long productId);
}
