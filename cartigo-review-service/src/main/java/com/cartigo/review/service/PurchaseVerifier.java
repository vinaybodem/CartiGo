package com.cartigo.review.service;

public interface PurchaseVerifier {
    boolean hasPurchased(Long userId, Long productId);
}
